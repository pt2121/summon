package com.prt2121.summon

/**
 * Created by pt2121 on 12/4/15.
 */
import android.Manifest.permission.READ_CONTACTS
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView
import com.prt2121.summon.model.Contact
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class ContactsFragment : Fragment(), ContactViewHolder.ClickListener {
  private var filter: String? = null
  private var recyclerView: RecyclerView? = null
  private val FILTER_KEY = "FILTER_KEY"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
    retainInstance = true
    if (savedInstanceState != null) {
      filter = savedInstanceState.getString(FILTER_KEY)
    }
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val root = inflater!!.inflate(R.layout.fragment_contacts, container, false)
    recyclerView = root.findViewById(R.id.recycler_view_contacts) as RecyclerView
    recyclerView!!.layoutManager = LinearLayoutManager(activity)
    recyclerView!!.itemAnimator = DefaultItemAnimator()
    recyclerView?.adapter = ContactsAdapter(null, this@ContactsFragment)
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    if (ContextCompat.checkSelfPermission(activity, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
      initLoader()
    }
  }

  fun initLoader() = loaderManager.initLoader(0, null, loaderCallbacks)

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString(FILTER_KEY, filter)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.options_menu, menu)

    val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
    val searchView = menu.findItem(R.id.menu_search).actionView as SearchView?
    if (searchView != null) {
      searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
      searchView.setIconifiedByDefault(false)
      RxSearchView.queryTextChanges(searchView)
          .filter { it.length > 3 }
          .throttleWithTimeout(2, TimeUnit.MILLISECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            filter = it.toString()
            loaderManager.restartLoader(0, null, loaderCallbacks)
          }, { e ->
            println(e.message)
          })
    }
  }

  override fun onItemViewClick(view: View, contact: Contact) {
    val intent = Intent(activity, RequestActivity::class.java)
    intent.putExtra(RequestActivity.NAME_EXTRA, contact.name)
    intent.putExtra(RequestActivity.PHONE_NUMBER_EXTRA, contact.phoneNumber)
    intent.putExtra(RequestActivity.PICTURE_URI_EXTRA, contact.profilePic)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view.findViewById(R.id.profile_image_view), "profile")
      activity.startActivity(intent, options.toBundle())
    } else {
      activity.startActivity(intent)
    }
  }

  private val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
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
          SELECTION,
          null,
          null)
    }

    override fun onLoadFinished(objectLoader: Loader<Cursor>, c: Cursor) {
      recyclerView?.adapter = ContactsAdapter(c, this@ContactsFragment)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {
      recyclerView?.adapter = null
    }
  }

  companion object {
    val TAG = ContactsFragment::class.java.simpleName
    private val PROJECTION = arrayOf(ContactsContract.Contacts._ID,
        ContactsContract.Contacts.LOOKUP_KEY,
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
        ContactsContract.Contacts.HAS_PHONE_NUMBER)
    private val SELECTION = "${ContactsContract.Contacts.HAS_PHONE_NUMBER} > 0 "
  }
}