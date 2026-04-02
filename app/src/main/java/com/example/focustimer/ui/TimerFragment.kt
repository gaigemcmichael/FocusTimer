package com.example.focustimer.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.focustimer.R
import com.example.focustimer.data.FocusTimerApplication
import com.example.focustimer.data.model.FocusSession
import com.example.focustimer.data.viewmodel.UserViewModel
import com.example.focustimer.databinding.FragmentTimerBinding
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

private const val TAG = "TimerFragment"
private const val PICKUP_THRESHOLD = 13.0f

class TimerFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private val args: TimerFragmentArgs by navArgs()
    private val userViewModel: UserViewModel by activityViewModels()
    
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var isTimerRunning = false
    private var initialTimeInMillis: Long = 0

    // Session data for database
    private var sessionStartTime: Date? = null
    private var pauseCount = 0
    private var roundsCompleted = 0 
    private var isBreak = false

    // Sensor variables
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var pickupCount = 0
    private var isPhoneFlat = true

    // Flowmodoro specific variables
    private var isFlowmodoroFocus = false
    private var isFlowmodoroBreak = false
    private var flowmodoroFocusTimeInMillis: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val countUpRunnable = object : Runnable {
        override fun run() {
            flowmodoroFocusTimeInMillis += 1000
            updateDisplay(flowmodoroFocusTimeInMillis)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val timerType = args.timerType
        binding.timerTypeLabel.text = timerType
        
        updatePickupCountDisplay()
        setupTimerDisplay(timerType)

        binding.startPauseButton.setOnClickListener {
            if (timerType == "Flowmodoro") {
                handleFlowmodoroClick()
            } else if (isTimerRunning) {
                pauseTimer()
            } else {
                if (timerType == "Classic" && timeLeftInMillis == 0L) {
                    setClassicTimer()
                } else {
                    startTimer()
                }
            }
        }

        binding.resetButton.setOnClickListener {
            if (sessionStartTime != null) {
                saveFocusSession()
            }
            resetTimer(timerType)
        }

        binding.endSessionButton.setOnClickListener {
            saveFocusSession(navigateToHome = true)
        }
    }

    private fun setupTimerDisplay(type: String) {
        when (type) {
            "Pomodoro" -> {
                timeLeftInMillis = 25 * 60 * 1000L
                initialTimeInMillis = timeLeftInMillis
                isBreak = false
                updateCountDownText()
                binding.startPauseButton.setText(R.string.focus_button_text)
            }
            "Classic" -> {
                binding.timerDisplay.isVisible = false
                binding.editTimerContainer.isVisible = true
                binding.startPauseButton.setText(R.string.set_and_start_button_text)
            }
            "Flowmodoro" -> {
                timeLeftInMillis = 0
                flowmodoroFocusTimeInMillis = 0
                isFlowmodoroBreak = false
                isFlowmodoroFocus = false
                updateDisplay(0)
                binding.startPauseButton.setText(R.string.start_focus_button_text)
            }
        }
    }

    private fun handleFlowmodoroClick() {
        if (!isTimerRunning && !isFlowmodoroBreak) {
            isTimerRunning = true
            isFlowmodoroFocus = true
            if (sessionStartTime == null) sessionStartTime = Date()
            binding.startPauseButton.setText(R.string.stop_and_start_break_button_text)
            handler.post(countUpRunnable)
        } else if (isFlowmodoroFocus) {
            handler.removeCallbacks(countUpRunnable)
            isFlowmodoroFocus = false
            isFlowmodoroBreak = true
            isTimerRunning = false
            
            roundsCompleted++
            
            timeLeftInMillis = flowmodoroFocusTimeInMillis / 5
            initialTimeInMillis = timeLeftInMillis
            
            if (timeLeftInMillis < 1000) {
                Toast.makeText(requireContext(), R.string.flowmodoro_min_focus_toast, Toast.LENGTH_SHORT).show()
                resetTimer("Flowmodoro")
            } else {
                startTimer() 
            }
        } else if (isFlowmodoroBreak) {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }
    }

    private fun setClassicTimer() {
        val minutesStr = binding.editMinutes.text.toString()
        val secondsStr = binding.editSeconds.text.toString()

        val minutes = minutesStr.toLongOrNull() ?: 0L
        val seconds = secondsStr.toLongOrNull() ?: 0L

        if (minutes == 0L && seconds == 0L) {
            Toast.makeText(requireContext(), R.string.please_enter_time_toast, Toast.LENGTH_SHORT).show()
            return
        }

        timeLeftInMillis = (minutes * 60 + seconds) * 1000L
        initialTimeInMillis = timeLeftInMillis
        
        binding.editTimerContainer.isVisible = false
        binding.timerDisplay.isVisible = true
        
        updateCountDownText()
        startTimer()
    }

    private fun startTimer() {
        if (sessionStartTime == null) sessionStartTime = Date()
        
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                isTimerRunning = false
                Toast.makeText(requireContext(), R.string.times_up_toast, Toast.LENGTH_SHORT).show()
                
                if (args.timerType == "Pomodoro") {
                    handlePomodoroFinish()
                } else if (args.timerType == "Flowmodoro") {
                    isFlowmodoroBreak = false
                    flowmodoroFocusTimeInMillis = 0
                    binding.startPauseButton.setText(R.string.start_focus_button_text)
                } else {
                    roundsCompleted++
                    binding.startPauseButton.setText(R.string.start_button_text)
                }
            }
        }.start()

        isTimerRunning = true
        binding.startPauseButton.setText(R.string.pause_button_text)
    }

    private fun handlePomodoroFinish() {
        if (!isBreak) {
            roundsCompleted++
            isBreak = true
            
            if (roundsCompleted % 4 == 0) {
                timeLeftInMillis = 25 * 60 * 1000L
            } else {
                timeLeftInMillis = 5 * 60 * 1000L
            }
            binding.startPauseButton.setText(R.string.break_button_text)
        } else {
            isBreak = false
            timeLeftInMillis = 25 * 60 * 1000L
            binding.startPauseButton.setText(R.string.focus_button_text)
        }
        updateCountDownText()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        pauseCount++
        binding.startPauseButton.setText(R.string.resume_button_text)
    }

    private fun saveFocusSession(navigateToHome: Boolean = false) {
        val currentUser = userViewModel.userResult.value?.username
        val start = sessionStartTime

        if (currentUser == null) {
            Log.e(TAG, "Save failed: No user logged in")
            if (navigateToHome) {
                if (findNavController().currentDestination?.id == R.id.timerFragment) {
                    findNavController().navigate(R.id.action_timerFragment_to_homeFragment)
                }
            }
            return
        }

        if (start == null) {
            Log.d(TAG, "Save skipped: No session start time (timer never started)")
            if (navigateToHome) {
                if (findNavController().currentDestination?.id == R.id.timerFragment) {
                    findNavController().navigate(R.id.action_timerFragment_to_homeFragment)
                }
            }
            return
        }

        val end = Date()
        val score = max(0, 100 - (pickupCount * 10))
        val finalRounds = roundsCompleted
        val timerType = args.timerType

        val session = FocusSession(
            startTime = start,
            endTime = end,
            numPickups = pickupCount,
            numPauses = pauseCount,
            focusScore = score,
            numRounds = finalRounds,
            userName = currentUser,
            focusMethodId = timerType.uppercase()
        )

        // Clear session state immediately so it's not saved again
        sessionStartTime = null

        lifecycleScope.launch {
            try {
                (requireActivity().application as FocusTimerApplication).timerRepository.insertSession(session)
                Log.d(TAG, "Focus Session Saved successfully: $session")

                context?.let {
                    Toast.makeText(it, "Session logged! Rounds: $finalRounds, Score: $score", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving focus session", e)
            } finally {
                if (navigateToHome) {
                    if (findNavController().currentDestination?.id == R.id.timerFragment) {
                        findNavController().navigate(R.id.action_timerFragment_to_homeFragment)
                    }
                }
            }
        }
    }

    private fun resetTimer(type: String) {
        countDownTimer?.cancel()
        handler.removeCallbacks(countUpRunnable)
        isTimerRunning = false
        pickupCount = 0
        pauseCount = 0
        roundsCompleted = 0
        sessionStartTime = null
        isBreak = false
        
        updatePickupCountDisplay()
        
        if (type == "Classic") {
            timeLeftInMillis = 0
            binding.timerDisplay.isVisible = false
            binding.editTimerContainer.isVisible = true
            binding.editMinutes.text.clear()
            binding.editSeconds.text.clear()
            binding.startPauseButton.setText(R.string.set_and_start_button_text)
        } else if (type == "Flowmodoro") {
            flowmodoroFocusTimeInMillis = 0
            timeLeftInMillis = 0
            isFlowmodoroBreak = false
            isFlowmodoroFocus = false
            updateDisplay(0)
            binding.startPauseButton.setText(R.string.start_focus_button_text)
        } else {
            timeLeftInMillis = 25 * 60 * 1000L
            updateCountDownText()
            binding.startPauseButton.setText(R.string.focus_button_text)
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { acc ->
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER && isTimerRunning) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val gForce = sqrt(x * x + y * y + z * z)
            
            if (gForce > PICKUP_THRESHOLD && isPhoneFlat) {
                pickupCount++
                updatePickupCountDisplay()
                isPhoneFlat = false
                Log.d(TAG, "Phone picked up! Count: $pickupCount")
            } 
            
            val isHorizontal = abs(z) > 9.0 && abs(x) < 1.5 && abs(y) < 1.5
            if (isHorizontal && gForce < 10.5f) {
                isPhoneFlat = true
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updatePickupCountDisplay() {
        binding.pickupCountText.text = getString(R.string.pick_up_count_label, pickupCount)
    }

    private fun updateDisplay(timeInMillis: Long) {
        val minutes = (timeInMillis / 1000) / 60
        val seconds = (timeInMillis / 1000) % 60
        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.timerDisplay.text = timeFormatted
    }

    private fun updateCountDownText() {
        updateDisplay(timeLeftInMillis)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (sessionStartTime != null) {
            saveFocusSession()
        }
        countDownTimer?.cancel()
        handler.removeCallbacks(countUpRunnable)
        _binding = null
    }
}
