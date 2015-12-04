package com.prt2121.summon

/**
 * Created by pt2121 on 12/4/15.
 */
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ContactsFragment : Fragment() {
  private var filter: String? = null
  private var recyclerView: RecyclerView? = null

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val root = inflater!!.inflate(R.layout.fragment_contacts, container, false)
    recyclerView = root.findViewById(R.id.recycler_view_contacts) as RecyclerView
    recyclerView!!.layoutManager = LinearLayoutManager(activity)
    recyclerView!!.itemAnimator = DefaultItemAnimator()
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    loaderManager.initLoader(0, null, object : LoaderManager.LoaderCallbacks<Cursor> {
      override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        val contentUri = if (filter != null) {
          Uri.withAppendedPath(
              ContactsContract.Contacts.CONTENT_FILTER_URI,
              Uri.encode(filter))
        } else {
          ContactsContract.Contacts.CONTENT_URI
        }

        return CursorLoader(
            activity,
            contentUri,
            PROJECTION,
            null,
            null,
            null)
      }

      override fun onLoadFinished(objectLoader: Loader<Cursor>, c: Cursor) {
        recyclerView?.adapter = ContactsAdapter(c)
      }

      override fun onLoaderReset(cursorLoader: Loader<Cursor>) {
        recyclerView?.adapter = null
      }
    })
  }

  companion object {
    private val PROJECTION = arrayOf(ContactsContract.Contacts._ID,
        ContactsContract.Contacts.LOOKUP_KEY,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
        ContactsContract.Contacts.HAS_PHONE_NUMBER)
  }
}