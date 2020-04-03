package com.ap.bharosaadvisor.data;

public class LearnVideo
{
    public String title;
    public String link;
    public String thumbnail;

    public LearnVideo(String _link, String _title)
    {
        title = _title;
        link = _link.substring(_link.length() - 11);
        thumbnail = "https://img.youtube.com/vi/" + link + "/0.jpg";
    }
}
