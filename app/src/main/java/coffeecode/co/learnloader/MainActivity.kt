package coffeecode.co.learnloader

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    companion object{
        private const val CONTACT_REQUEST_CODE = 101
        private const val CALL_REQUEST_CODE = 102
        private const val CONTACT_LOAD = 110
        private const val CONTACT_SELECT = 120
        const val TAG = "ContactApp"
    }

    private var mAdapter: ContactAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lvContact.visibility = View.INVISIBLE
        progressBar.visibility = View.GONE

        mAdapter = ContactAdapter(this, null, true)
        lvContact.adapter = mAdapter
        lvContact.onItemClickListener = this

        if (PermissionManager.isGranted(this, Manifest.permission.READ_CONTACTS)){
            supportLoaderManager.initLoader(CONTACT_LOAD, null, this)
        }else{
            PermissionManager.check(this, Manifest.permission.READ_CONTACTS, CONTACT_REQUEST_CODE)
        }
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        var mCursorLoader: CursorLoader? = null
        if (id == CONTACT_LOAD){
            progressBar.visibility = View.VISIBLE

            val projectionFields = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI
            )

            mCursorLoader = CursorLoader(this,
                ContactsContract.Contacts.CONTENT_URI,
                projectionFields,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
        }else if (id == CONTACT_SELECT){
            val phoneProjectionFields = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)

            mCursorLoader = CursorLoader(this,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                phoneProjectionFields,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + " AND " +
                        ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1",
                arrayOf(args?.getString("id")),
                null)
        }
         return mCursorLoader!!
    }

    @SuppressLint("MissingPermission")
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        Log.d(TAG, "LoadFinished")

        if (loader.id == CONTACT_LOAD){
            if (data?.count!! > 0){
                lvContact.visibility = View.VISIBLE
                mAdapter?.swapCursor(data)
            }
            progressBar.visibility = View.GONE
        }else if (loader.id == CONTACT_SELECT){
            var contactNumber: String? = null
            if (data != null) {
                if (data.moveToFirst()) {
                    contactNumber = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                }
            }

            if (PermissionManager.isGranted(this, Manifest.permission.CALL_PHONE)) {
                val dialIntent = Intent(
                    Intent.ACTION_CALL,
                    Uri.parse("tel:$contactNumber")
                )
                startActivity(dialIntent)
            } else {
                PermissionManager.check(this, Manifest.permission.CALL_PHONE, CALL_REQUEST_CODE)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        if (loader.id == CONTACT_LOAD) {
            progressBar.visibility = View.GONE
            mAdapter?.swapCursor(null)
            Log.d(TAG, "LoaderReset")
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val cursor = parent?.adapter?.getItem(position) as Cursor
        val mContactId = cursor.getLong(0)
        Log.d(TAG, "Position : $position $mContactId")
        getPhoneNumber(mContactId.toString())
    }

    private fun getPhoneNumber(contactID: String) {
        val bundle = Bundle()
        bundle.putString("id", contactID)
        supportLoaderManager.restartLoader(CONTACT_SELECT, bundle, this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CONTACT_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    supportLoaderManager.initLoader(CONTACT_LOAD, null, this)
                    Toast.makeText(this, "Contact permission diterima", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Contact permission ditolak", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == CALL_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Call permission diterima", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Call permission ditolak", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
