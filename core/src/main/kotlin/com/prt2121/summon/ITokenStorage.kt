package com.prt2121.summon

/**
 * Created by pt2121 on 12/1/15.
 */
interface ITokenStorage {
  fun save(token: String)
  fun retrieve(): String?
}