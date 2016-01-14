package pl.rcebula.volumecontroller;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sairamkrishna on 4/11/2015.
 */
public class HandleXml
{
    private List<Song> songs = new ArrayList<>();
    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;

    public HandleXml(String url)
    {
        this.urlString = url;
    }

    public List<HandleXml.Song> getSongs()
    {
        return songs;
    }

    private void parseXMLAndStoreIt(XmlPullParser myParser)
            throws IOException, XmlPullParserException
    {
        int event;
        String text = null;
        String title = null;
        Float rate = null;
        boolean playing = false;

        event = myParser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT)
        {
            String name = myParser.getName();

            switch (event)
            {
                case XmlPullParser.START_TAG:
                    if (name.equals("song"))
                    {
                        rate = Float.parseFloat(myParser.getAttributeValue(null, "rate"));
                        String p = myParser.getAttributeValue(null, "playing");
                        playing = (p == null) ? false : true;
                    }
                    break;

                case XmlPullParser.TEXT:
                    text = myParser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if (name.equals("title"))
                    {
                        title = text;
                    }
                    else if (name.equals("song"))
                    {
                        songs.add(new Song(title, rate, playing));
                        playing = false;
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
        conn.setUseCaches(false);
        conn.setDefaultUseCaches(false);
        conn.connect();

        InputStream stream = conn.getInputStream();
        xmlFactoryObject = XmlPullParserFactory.newInstance();
        XmlPullParser myparser = xmlFactoryObject.newPullParser();

        myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        myparser.setInput(stream, null);

        parseXMLAndStoreIt(myparser);
        stream.close();
    }

    public class Song
    {
        private String title;
        private float rate;
        private boolean playing;

        public Song(String title, float rate, boolean playing)
        {
            this.title = title;
            this.rate = rate;
            this.playing = playing;
        }

        public String getTitle()
        {
            return title;
        }

        public float getRate()
        {
            return rate;
        }

        public boolean isPlaying()
        {
            return playing;
        }

        @Override
        public String toString()
        {
            return title + ": " + Float.toString(rate);
        }
    }
}
