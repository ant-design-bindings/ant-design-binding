package adb.component.menu

import com.thoughtworks.binding.{dom, Binding}
import com.thoughtworks.binding.Binding.{BindingSeq, Constant, Constants, SingletonBindingSeq, Var}
import org.scalajs.dom.raw.{Event, Node}

object Menu {

  @dom
  def menu(children: BindingSeq[Node], isDark: Boolean = false): Binding[Node] = {
    <ul class={"ant-menu ant-menu-root ant-menu-inline " + (if(isDark) "ant-menu-dark" else "ant-menu-light")}>
      {children.map(v => v)}
    </ul>
  }

  @dom
  def menuItem(`class`: Binding[String] = Constant(""), style: Binding[String] = Constant(""), onclick: Event => Unit = _ => {})(
      children: BindingSeq[Node]): Binding[Node] = {
    <li class={"ant-menu-item " + `class`.bind} style={style.bind} onclick={onclick}>
      {children.map(v => v)}
    </li>
  }

  case class NavigationItem[T](selectedItem: T, content: Binding[Node])

  def navigation[T](navigationItems: Seq[NavigationItem[T]], selectedItem: Var[Option[T]], isDark: Boolean = false): Binding[Node] = {
    menu(
      {
        for {
          ni <- Constants(navigationItems: _*)
        } yield {
          menuItem(
            `class` = Constant(if (selectedItem.bind.contains(ni.selectedItem)) "ant-menu-item-selected" else ""),
            style = Constant("padding-left: 24px;"),
            onclick = (_: Event) => {
              selectedItem.value = Some(ni.selectedItem)
            }
          ) {
            SingletonBindingSeq(ni.content)
          }.bind
        }
      },
      isDark
    )
  }

}
