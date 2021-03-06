package adb.component.button

import scala.collection.immutable

import com.thoughtworks.binding.{dom, Binding}
import enumeratum._
import org.scalajs.dom.html.Button
import com.thoughtworks.binding.bindable._

object Button {

  sealed abstract class ButtonType(private[button] val className: String) extends EnumEntry

  case object ButtonType extends Enum[ButtonType] {

    val values: immutable.IndexedSeq[ButtonType] = findValues

    case object Default extends ButtonType("")

    case object Primary extends ButtonType("ant-btn-primary")

    case object Dashed extends ButtonType("ant-btn-dashed")

    case object Danger extends ButtonType("ant-btn-danger")

  }

  @dom
  def button[Text: Bindable.Lt[?, String], ButtonTypeT: Bindable.Lt[?, ButtonType]](text: Text, buttonType: ButtonTypeT = ButtonType.Default): Binding[Button] = {
    <button type="button" class={"ant-btn " + buttonType.bind.className}>
      <span>{text.bind}</span>
    </button>
  }

}
