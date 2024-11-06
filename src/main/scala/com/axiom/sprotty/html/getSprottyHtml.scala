package com.axiom.sprotty.html

import typings.vscode.mod as vscode
import scala.util.Random

object SprottyHtml:
    // Function to generate dynamic HTML content
    def getSprottyHtml(webview: vscode.Webview, context: vscode.ExtensionContext): String = {
        val nonce = getNonce()
        // Create an HTML unordered list from the content
        // val listItems = patient.map(item => s"<li>${item.unitNumber}</li>").mkString("\n")
        val scriptUri = webview.asWebviewUri(vscode.Uri.joinPath(context.extensionUri, "media", "sprotty.js")).toString;
        val stylestUri = webview.asWebviewUri(vscode.Uri.joinPath(context.extensionUri, "media", "styles.css")).toString;
        // println(s"Script URI: $scriptUri");
        s"""
            |<!DOCTYPE html>
            |<html lang="en">
            |<head>
            |    <meta charset="UTF-8">
            |    <meta http-equiv="Content-Security-Policy" content="default-src 'none'; style-src ${webview.cspSource}; img-src ${webview.cspSource} https:; script-src 'nonce-${nonce}'; connect-src 'self' http://localhost:8080;">
            |    <meta name="viewport" content="width=device-width, initial-scale=1.0">
            |    <title>Showing Sprotty</title>
            |    <link rel="stylesheet" href="${stylestUri}" />
            |</head>
            |<body>
            |    <script type="module" src="${scriptUri}" nonce="${nonce}"></script>
            |    <h1>Sprotty</h1>
            |    <div id="sprotty-container"></div>
            |</body>
            |</html>
        """.stripMargin
    }

    def getNonce(): String = {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        (1 to 32).map(_ => possible(Random.nextInt(possible.length))).mkString
    }