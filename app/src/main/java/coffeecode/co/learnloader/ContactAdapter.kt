package coffeecode.co.learnloader

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView

class ContactAdapter(private val context: Context?, c: Cursor?, autoRequest: Boolean) : CursorAdapter(context, c, autoRequest) {


    override fun newView(p0: Context?, p1: Cursor?, p2: ViewGroup?): View =
        LayoutInflater.from(context).inflate(R.layout.item_row_contact, p2, false)

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        if (cursor != null){
            val tvName = view?.findViewById<TextView>(R.id.tvItemName)
            val imgUser = view?.findViewById<CircleImageView>(R.id.imgItemUser)

            tvName?.text = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
            if (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)) != null){
                imgUser?.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))))
            }else{
                imgUser?.setImageResource(R.drawable.ic_person_black_24dp)
            }
        }
    }
}