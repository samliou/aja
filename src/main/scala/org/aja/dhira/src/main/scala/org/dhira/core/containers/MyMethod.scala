package org.dhira.core.containers

/**
 * Created by mageswaran on 10/9/16.
 */
trait MyMethod[I, O] {
  def call (obj: I): O
}
