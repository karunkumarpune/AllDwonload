package fluper.com.pdfdwonload

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.util.*

class Main2Activity : AppCompatActivity() {
   private var downloadManager: DownloadManager? = null
   private var refid: Long = 0
   private var Download_Uri: Uri? = null
   internal var list = ArrayList<Long>()
   
   private var file: File? = null
   
   companion object {
      
      var NOTIFICATION_ID = 1
      private val filename = "sample"
      private val folderName = "/Download//Immigration/"
      private val folderNameSubpath = "/Immigration//"
     // private val url = "http://rusenergyweek.com/upload/iblock/1b9/1b9cb0045fcda0e07be921ec922f5191.pdf"
      //private val url = "https://www.android-examples.com/wp-content/uploads/2016/04/Thunder-rumble.mp3"
      private val url = "https://ia800201.us.archive.org/22/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4"
    //  private val extenstion_filename = ".pdf"
     // private val extenstion_filename = ".mp3"
      private val extenstion_filename = ".mp4"
   
   }
   
   
   
   
   //permission is automatically granted on sdk<23 upon installation
   val isStoragePermissionGranted: Boolean
      get() {
         if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
               return true
            } else {
               ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
               return false
            }
         } else {
            return true
         }
      }
   
   
   
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)
      
      downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
      registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
      Download_Uri = Uri.parse(url)
      val btnSingle = findViewById<View>(R.id.single) as TextView
      val Display = findViewById<View>(R.id.multiple) as TextView
      
      
      if (!isStoragePermissionGranted) {
      }
      
      Display.setOnClickListener {
       try {
          file = File(Environment.getExternalStorageDirectory().path + folderName + filename + extenstion_filename)
   
          if (file!!.exists()) {
             val path = Uri.fromFile(file)
             val intent = Intent(Intent.ACTION_VIEW)
             //  intent.setDataAndType(path, "image/*")
            // intent.setDataAndType(path, "audio/*")
             intent.setDataAndType(path, "video/*")
             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
             startActivity(intent)
             finish()
          } else {
             Toast.makeText(applicationContext, "Please download file..", Toast.LENGTH_SHORT).show()
          }
       }catch (e:Exception){
          Toast.makeText(applicationContext, "deices not support", Toast.LENGTH_SHORT).show()
       }
       
      }
      
      
      
      
      btnSingle.setOnClickListener {
         file = File(Environment.getExternalStorageDirectory().path +folderName+filename + extenstion_filename)
         if (file!!.exists()) {
            Toast.makeText(applicationContext, "Already exists", Toast.LENGTH_SHORT).show()
         } else {
            list.clear()
            val request = DownloadManager.Request(Download_Uri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setAllowedOverRoaming(false)
            request.setTitle("Immigration Downloading " + "Sample" + extenstion_filename)
            request.setDescription("Downloading " + "Sample" + extenstion_filename)
            request.setVisibleInDownloadsUi(true)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, folderNameSubpath+filename+extenstion_filename)
            refid = downloadManager!!.enqueue(request)
            Log.e("OUT", "" + refid)
            list.add(refid)
         }
      }
   }
   
   
   private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
      override fun onReceive(ctxt: Context, intent: Intent) {
         val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
         Log.e("IN", "" + referenceId)
         list.remove(referenceId)
         if (list.isEmpty())
         {
   
            val intents = Intent(Intent.ACTION_VIEW)
            val pendingIntent: PendingIntent
            try {
               val path = Uri.fromFile(file)
               //intents.setDataAndType(path, "image/*")
              // intents.setDataAndType(path, "application/pdf")
              // intents.setDataAndType(path, "audio/*")
               intents.setDataAndType(path, "video/*")
               startActivity(intents)
            }catch (e:Exception){
               Toast.makeText(applicationContext, "deices not support", Toast.LENGTH_SHORT).show()
            }
               pendingIntent = PendingIntent.getActivity(this@Main2Activity, 0, intents, PendingIntent.FLAG_ONE_SHOT)
               val mNotifyBuilder = NotificationCompat.Builder(this@Main2Activity, "")
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_launcher))
                .setContentText("dwonload")
               val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            if (NOTIFICATION_ID > 1073741824) {
               NOTIFICATION_ID = 0
            }
            notificationManager.notify(NOTIFICATION_ID++, mNotifyBuilder.build())
         }
      }
   }
   

   
   
   override fun onDestroy() {
      super.onDestroy()
      unregisterReceiver(onComplete)
   }
   
   override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults)
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
         // permission granted
      }
   }
}