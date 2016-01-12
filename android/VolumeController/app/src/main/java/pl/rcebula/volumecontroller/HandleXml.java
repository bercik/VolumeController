package pl.rcebula.volumecontroller;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sairamkrishna on 4/11/2015.
 */
public class HandleXml
{
    private List<String> songTitles = new ArrayList<>();
    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;

    public HandleXml(String url)
    {
        this.urlString = url;
    }

    public List<String> getSongTitles()
    {
        return songTitles;
    }

    private void parseXMLAndStoreIt(XmlPullParser myParser)
            throws IOException, XmlPullParserException
    {
        int event;
        String text = null;

        event = myParser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT)
        {
            String name = myParser.getName();

            switch (event)
            {
                case XmlPullParser.START_TAG:
                    break;

                case XmlPullParser.TEXT:
                    text = myParser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if (name.equals("title"))
                    {
                        songTitles.add(text);
                    }

                    break;
            }
            event = myParser.next();
        }
    }

    public void fetchXML() throws IOException, XmlPullParserException
    {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        InputStream stream = conn.getInputStream();
        xmlFactoryObject = XmlPullParserFactory.newInstance();
        XmlPullParser myparser = xmlFactoryObject.newPullParser();

        myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        myparser.setInput(stream, null);

        parseXMLAndStoreIt(myparser);
        stream.close();
    }
}
