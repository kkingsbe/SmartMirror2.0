package sample;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.webcerebrium.binance.api.BinanceApi;
import com.webcerebrium.binance.api.BinanceApiException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.util.Calendar;

public class Controller {

    public Label clockWidget;
    public Label dateLabel;
    public ImageView weatherIcon;
    public Label weatherConditionLabel;
    public Label headlinesLabel;
    public Label btcPriceLabel;
    public Label ethPriceLabel;
    String location = "Kensington, Md"; //Set this to your location
    String condition;
    int temp;
    int numHeadlines = 7;

    public void initialize(){
        startClock();
        startWeatherService();
        startNewsService();
        startCryptoPricesService();
    }

    private void startCryptoPricesService() {
        BigDecimal btcPriceDecimel = null;
        try {
            btcPriceDecimel = new BinanceApi().pricesMap().get("BTCUSDT").setScale(0, RoundingMode.HALF_UP);
            System.out.println(btcPriceDecimel);
        } catch (BinanceApiException e) {
            e.printStackTrace();
        }
        btcPriceLabel.setText("BTC: $" + btcPriceDecimel.toString());
    }

    private void startNewsService() {
        Timeline getNews = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            RSSFeedParser parser = new RSSFeedParser(
                    "https://news.google.com/news/rss/?ned=us&gl=US&hl=en");
            Feed feed = parser.readFeed();
            String headlines = "";
            System.out.println(feed);
            int line = 0;
            for (FeedMessage message : feed.getMessages()) {
                System.out.println(message.title);
                if (!message.title.startsWith("More Top Stories - Google News")){
                    headlines += message.title + "\n" + "\n";
                    line ++;
                }
                if (line > numHeadlines){
                    break;
                }
            }
            headlinesLabel.setText(headlines);
        }),
                new KeyFrame(Duration.seconds(60*10))
        );
        getNews.setCycleCount(Animation.INDEFINITE);
        getNews.play();
    }

    private void startWeatherService() {
        Timeline getWeather = new Timeline(new KeyFrame(Duration.ZERO, e -> {

            JsonNode jn = null;
            try {
                jn = Unirest
                        .get(" https://query.yahooapis.com/v1/public/yql")
                        .queryString("format", "json")
                        .queryString("q", "select item.condition from weather.forecast where woeid in (select woeid from geo.places(1) where text=\""+location+"\")")
                        .asJson()
                        .getBody();
            } catch (UnirestException e1) {
                e1.printStackTrace();
            }

            org.json.JSONObject Condition = jn.getObject()
                    .getJSONObject("query")
                    .getJSONObject("results")
                    .getJSONObject("channel")
                    .getJSONObject("item")
                    .getJSONObject("condition");
            //System.out.println(Condition);
            condition = Condition.getString("text");
            temp = Integer.parseInt(Condition.getString("temp"));
            System.out.println("Condition: " + condition);
            System.out.println("Temperature: " + temp);

            weatherConditionLabel.setText(condition);
            String icon = "";

            switch(condition.toLowerCase()){
                case "partly cloudy": icon = "partly-cloudy-day-xxl";
                    break;
                case "thunderstorms": icon = "storm-xxl";
                    break;
                case "severe thunderstorms": icon = "storm-xxl";
                    break;
                case "hurricane": icon = "storm-xxl";
                    break;
                case "drizzle": icon = "rain-xxl";
                    break;
                case "showers": icon = "rain-xxl";
                    break;
                case "snow flurries": icon = "snow-storm-xxl";
                    break;
                case "blowing snow": icon = "snow-storm-xxl";
                    break;
                case "light snow showers": icon = "snow-storm-xxl";
                    break;
                case "snow": icon = "snow-storm-xxl";
                    break;
                case "heavy snow": icon = "snow-storm-xxl";
                    break;
                case "foggy": icon = "fog-day-xxl";
                    break;
                case "cloudy": icon = "cloud-4-512";
                    break;
                case "clear": icon = "sun-5-xxl";
                    break;
                case "sunny": icon = "sun-5-xxl";
                    break;
                default: icon = "sun-5-xxl";
                    break;
            }
            File file = new File("Resources/Icons/Weather/"+icon+".png");
            try {
                Image iconImage = new Image(file.toURI().toURL().toString());
                weatherIcon.setImage(iconImage);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }),
                new KeyFrame(Duration.seconds(60*10))
        );
        getWeather.setCycleCount(Animation.INDEFINITE);
        getWeather.play();
    }

    public void startClock(){
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Calendar cal = Calendar.getInstance();
            int second = cal.get(Calendar.SECOND);
            int minute = cal.get(Calendar.MINUTE);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            String dayOfWeekString = "";
            String monthString = "";
            clockWidget.setText(hour + ":" + (minute) + ":" + second);

            switch (dayOfWeek){
                case 1: dayOfWeekString = "Sunday";
                    break;
                case 2: dayOfWeekString = "Monday";
                    break;
                case 3: dayOfWeekString = "Tuesday";
                    break;
                case 4: dayOfWeekString = "Wednsday";
                    break;
                case 5: dayOfWeekString = "Thursday";
                    break;
                case 6: dayOfWeekString = "Friday";
                    break;
                case 7: dayOfWeekString = "Saturday";
                    break;

            }

            switch (month){
                case 1: monthString = "January";
                    break;
                case 2: monthString = "Febuary";
                    break;
                case 3: monthString = "March";
                    break;
                case 4: monthString = "April";
                    break;
                case 5: monthString = "May";
                    break;
                case 6: monthString = "June";
                    break;
                case 7: monthString = "July";
                    break;
                case 8: monthString = "August";
                    break;
                case 9: monthString = "September";
                    break;
                case 10: monthString = "October";
                    break;
                case 11: monthString = "November";
                    break;
                case 12: monthString = "December";
                    break;
            }
            dateLabel.setText(dayOfWeekString + "\n" + monthString + ", " + year);
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}
