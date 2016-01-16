package pl.rcebula.volumecontroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robert on 16.01.16.
 */
public class Urls
{
    private List<Url> urls = new ArrayList<>();

    public Urls()
    {
        urls.add(new Url("Polskastacja",
                "http://www.polskastacja.pl/webplayer/index.php?channel=43&version=20150602"));
        urls.add(new Url("Spotify lata 80,90",
                "https://play.spotify.com/user/bercikos/playlist/66XMDnNTOdTfQp0DiKWG2t?play=true"));
        urls.add(new Url("Spotify weekly 21.12.2015",
                "https://play.spotify.com/user/bercikos/playlist/620HG6gZfZ7HogVLlh1r1z?play=true"));
    }

    public List<Url> getUrls()
    {
        return urls;
    }

    public class Url
    {
        private String description;
        private String url;

        public Url(String description, String url)
        {
            this.description = description;
            this.url = url;
        }

        public String getUrl()
        {
            return url;
        }

        public String getDescription()
        {
            return description;
        }
    }
}
