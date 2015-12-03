package com.prt2121.summon

import org.robovm.apple.contacts.CNContact
import org.robovm.apple.foundation.NSIndexPath
import org.robovm.apple.uikit.UITableView
import org.robovm.apple.uikit.UITableViewCell
import org.robovm.objc.annotation.CustomClass

/**
 * Created by pt2121 on 11/28/15.
 */
@CustomClass("ResultsTableViewController")
class ResultsTableViewController : BaseTableViewController() {
  var filteredContacts: List<CNContact>? = null

  override fun getNumberOfRowsInSection(tableView: UITableView, section: Long): Long {
    return filteredContacts!!.size.toLong()
  }

  override fun getCellForRow(tableView: UITableView, indexPath: NSIndexPath): UITableViewCell {
    val contact = filteredContacts!![indexPath.row.toInt()]

    val cell = getTableView().dequeueReusableCell(CELL_IDENTIFIER)
    configureCell(cell, contact)
    return cell
  }
}