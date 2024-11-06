import typings.vscode.mod as vscode
import typings.vscode.anon.Dispose
import typings.vscode.Thenable

import scala.collection.immutable
import scala.util.*
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.UndefOr

import concurrent.ExecutionContext.Implicits.global
import com.axiom.patientTracker.html.PatientsListHtml.getPatientsListHtml
import typings.std.PromiseLike
import com.axiom.sprotty.html.SprottyHtml.getSprottyHtml

object myext {
  @JSExportTopLevel("activate")
  def activate(context: vscode.ExtensionContext): Unit = {
    val path = js.Dynamic.global.require("path")
    val outputChannel = vscode.window.createOutputChannel("Aurora Constellations")
    outputChannel.appendLine("Welcome to Aurora Extension!")
    outputChannel.show(preserveFocus = true)

    def showHello(): js.Function1[Any, Any] =
      (arg) => {
        vscode.window.showInputBox().toFuture.onComplete {
          case Success(input) => vscode.window.showInformationMessage(s"Hello $input!")
          case Failure(e)     => println(e.getMessage)
        }
      }

    def sprotty(): js.Function1[Any, Any] = 
      (args) => {
        val panel = vscode.window.createWebviewPanel(
          "sprotty", // Internal identifier of the webview panel
          "Show Sprotty", // Title of the panel displayed to the user
          vscode.ViewColumn.One, // Editor column to show the new webview panel in
          js.Dynamic
            .literal( // Webview options
              enableScripts = true, // Allow JavaScript in the webview
              localResourceRoots = js.Array(
                vscode.Uri.file(path.join(context.extensionPath, "media").toString),
                vscode.Uri.file(path.join(context.extensionPath, "out").toString)
              )
            )
            .asInstanceOf[vscode.WebviewPanelOptions & vscode.WebviewOptions]
        )
        // Set the HTML content for the panel
        panel.webview.html = getSprottyHtml(panel.webview, context)
      }

    def showPatients(): js.Function1[Any, Any] =
      (args) => {
        val panel = vscode.window.createWebviewPanel(
          "Patients", // Internal identifier of the webview panel
          "Patient List", // Title of the panel displayed to the user
          vscode.ViewColumn.One, // Editor column to show the new webview panel in
          js.Dynamic
            .literal( // Webview options
              enableScripts = true, // Allow JavaScript in the webview
              localResourceRoots = js.Array(
                vscode.Uri.file(path.join(context.extensionPath, "media").toString),
                vscode.Uri.file(path.join(context.extensionPath, "out").toString)
              )
            )
            .asInstanceOf[vscode.WebviewPanelOptions & vscode.WebviewOptions]
        )
        // Set the HTML content for the panel
        panel.webview.html = getPatientsListHtml(panel.webview, context)
      }

    val commands = List(
      ("myext.helloWorld", showHello()),
      ("myext.patients", showPatients()),
      ("myext.sprotty", sprotty())
    )

    commands.foreach { case (name, fun) =>
      context.subscriptions.push(
        vscode.commands
          .registerCommand(name, fun)
          .asInstanceOf[Dispose]
      )
    }

  }
}
