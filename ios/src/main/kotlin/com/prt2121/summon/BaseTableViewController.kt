package com.prt2121.summon

import org.robovm.apple.contacts.CNContact
import org.robovm.apple.uikit.UINib
import org.robovm.apple.uikit.UITableViewCell
import org.robovm.apple.uikit.UITableViewController
import org.robovm.objc.annotation.CustomClass

/**
 * Created by pt2121 on 11/28/15.
 */
@CustomClass("BaseTableViewController")
open class BaseTableViewController : UITableViewController() {
  internal val CELL_IDENTIFIER = "cellID"
  private val TABLE_CELL_NIB_NAME = "TableCell"

  override fun viewDidLoad() {
    super.viewDidLoad()

    // we use a nib which contains the cell's view and this class as the files owner
    tableView.registerReusableCellNib(UINib(TABLE_CELL_NIB_NAME, null), CELL_IDENTIFIER)
  }

  fun configureCell(cell: UITableViewCell, contact: CNContact) {
    cell.textLabel.text = "${contact.givenName} ${contact.familyName}"
    val email = if (contact.emailAddresses.isNotEmpty()) " | ${contact.emailAddresses[0].value}" else ""
    cell.detailTextLabel.text = (if (contact.phoneNumbers.isNotEmpty()) contact.phoneNumbers[0].value.stringValue else "") + email
  }
}