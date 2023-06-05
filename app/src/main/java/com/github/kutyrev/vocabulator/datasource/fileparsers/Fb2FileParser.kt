package com.github.kutyrev.vocabulator.datasource.fileparsers

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Xml
import com.github.kutyrev.vocabulator.BuildConfig
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream


private const val FB_TAG = "FictionBook"
private const val PARAGRAPH_TAG = "p"
private const val TAG = "Fb2FileParser"

class Fb2FileParser(private val context: Context) :
    FileParser {

    override fun parseFile(uri: Uri): ParsingResult {
        val fileText = StringBuilder()

        val fileInputStream: InputStream? = context.contentResolver.openInputStream(uri)

        val resultArray: ArrayList<String?>?

        try {
            resultArray = fileInputStream?.let { parse(it) }
        } catch (e: IOException) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, e.toString())
            }
            return ParsingResult.IOException
        }

        for (line in resultArray!!) {
            fileText.append(" ").append(line)
        }

        fileInputStream?.close()

        return ParsingResult.SuccessfullParsing(fileText.toString().trim())
    }

    @Throws(IOException::class)
    private fun parse(fileInputStream: InputStream): ArrayList<String?>? {
        return try {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(fileInputStream, null)
            parser.nextTag()
            readFeed(parser)
        } catch (e: XmlPullParserException) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, e.toString())
            }
            ArrayList()
        } finally {
            fileInputStream.close()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): ArrayList<String?> {
        val entries = ArrayList<String?>()
        parser.require(XmlPullParser.START_TAG, null, FB_TAG)
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                parser.next()
                continue
            }
            val name = parser.name
            // Starts by looking for the entry tag
            if (name == PARAGRAPH_TAG) {
                entries.add(readEntry(parser))
            } else {
                parser.next()
            }
        }
        return entries
    }

    // Processes title tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readEntry(parser: XmlPullParser): String? {
        return readText(parser)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String? {
        var result: String? = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }
}
