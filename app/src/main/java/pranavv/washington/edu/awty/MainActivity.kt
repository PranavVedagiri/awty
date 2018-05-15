package pranavv.washington.edu.awty

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.jar.Manifest
import android.os.StrictMode




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var message = findViewById<EditText>(R.id.message)
        val phoneNumber = findViewById<EditText>(R.id.phoneNumber)
        val minutes = findViewById<EditText>(R.id.minutes)
        val start = findViewById<Button>(R.id.start)

        val audio = findViewById<Button>(R.id.audio)
        val video = findViewById<Button>(R.id.video)

        if (checkCallingOrSelfPermission(android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), 1)
        }

        start.setOnClickListener {
            if (checkPhone(phoneNumber) && checkMinutes(minutes)) {
                var numMessage = changeNumber(phoneNumber.text.toString())
                var numMinutes = minutes.text.toString().toInt()

                val intent = Intent("pranavv.washington.edu.awty")
                intent.putExtra("message", message.text.toString())
                intent.putExtra("phone", numMessage)
                val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
                val intentFilter = IntentFilter("pranavv.washington.edu.awty")
                registerReceiver(AlarmReceiver(), intentFilter)
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                if (start.text.toString() == "Start") {
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + numMinutes.toLong() * (60 * 1000), numMinutes.toLong() * (60 * 1000), pendingIntent)
                    start.setText("Stop")
                } else {
                    start.setText("Start")
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            }
        }

        audio.setOnClickListener{
            if(checkPhone(phoneNumber) && checkMinutes(minutes)){

               var attachment = "file:///Users/pranavvedagiri/Downloads/Kanye-West-Lift-Yourself.mp3"


                var num = changeNumber(phoneNumber.text.toString())

                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra("address", num)
                sendIntent.putExtra("sms_body", "Enjoy")

                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(attachment))
                sendIntent.type = "audio/mp3"

                sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                startActivity(sendIntent)
            }
        }

        video.setOnClickListener {
            if(checkPhone(phoneNumber) && checkMinutes(minutes)){
                var attachment = "file:///Users/pranavvedagiri/Downloads/Charlie%20bit%20my%20finger%20(%20original%20full%20version%20)%20(youtubemp4.to).mp4"

                var num = changeNumber(phoneNumber.text.toString())

                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.putExtra("address", num)
                sendIntent.putExtra("sms_body", "Enjoy")

                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(attachment))
                sendIntent.type = "video/mp4"

                sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                startActivity(sendIntent)
            }
        }
    }


    fun checkMinutes(minutes: EditText): Boolean{
        return !(minutes.text.toString().toInt() == 0 || minutes.text.toString().toInt() < 0 || minutes.text.toString() == "")
    }

    fun checkPhone(number: EditText): Boolean{
        return !(number.text.toString() == ""|| number.text.toString().length != 10)
    }

    fun changeNumber(number: String): String{
        return "(" + number.substring(0, 3) + ") " + number.substring(3, 6) + "-" + number.substring(6, 10)
    }
}

class AlarmReceiver: BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent!!.getStringExtra("message")
        val number = intent!!.getStringExtra("phone")
        Toast.makeText(context, number + ": " + message, Toast.LENGTH_SHORT).show()
        SmsManager.getDefault().sendTextMessage(number, null, message, null, null)
    }
}
