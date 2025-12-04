package MyPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//String inputData=request.getParameter("userInput");//reading input data from form method post
		//API setup
		String apiKey="Your_Api_Key";
		//get city from the form input
		String city=request.getParameter("city").trim();
		String encodedCity = URLEncoder.encode(city, "UTF-8");
		//create url for the WeatherApi request
		String apiUrl="https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + encodedCity;
		//System.out.println(apiUrl);
		 try {
			//Integrating api 
			URL url= new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			
			//Reading the data from the network
			InputStream inputStream = connection.getInputStream();
			InputStreamReader reader=new InputStreamReader(inputStream);
			
			//Store data in String
			StringBuilder responseContent=new StringBuilder();
			
			//Input lene ke liye from the reader , will create scanner class
			Scanner scanner=new Scanner(reader);
			
			while(scanner.hasNext()) {
				responseContent.append(scanner.nextLine());
			}
			scanner.close();
			//System.out.println(responseContent);
			
			
			//parsing the data into json
			
			Gson gson = new Gson();
	        JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
	        System.out.println(jsonObject);
	        
	        //Date and time
	        JsonObject location = jsonObject.getAsJsonObject("location");
	        String localTime = location.get("localtime").getAsString();
	        
	        //get humidity and wind speed
	        JsonObject current = jsonObject.getAsJsonObject("current");
	        int tempC = (int) current.get("temp_c").getAsDouble(); //temperature in celcius 
	        int humidity = current.get("humidity").getAsInt(); //humidity
	        double windKph = current.get("wind_kph").getAsDouble(); //wind in kph
	        
	        String condition = current
	                .getAsJsonObject("condition")
	                .get("text")
	                .getAsString();
	     // Convert localtime String into LocalDateTime
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	        LocalDateTime dateTime = LocalDateTime.parse(localTime, formatter);

	        // Get day name
	        String dayName = dateTime.getDayOfWeek().toString();  // THURSDAY

	        // Make it proper case (Thursday instead of THURSDAY)
	        String finalDay = dayName.substring(0,1) + dayName.substring(1).toLowerCase();

	        // Send to JSP
	        request.setAttribute("day", finalDay);
	        
	     // Set the data as request attributes (for sending to the JSP page)
	        request.setAttribute("date", localTime);           // local time from API
	        request.setAttribute("city", city);               // user input city
	        request.setAttribute("temperature", tempC);       // temperature in Celsius (int)
	        request.setAttribute("weatherCondition", condition); 
	        request.setAttribute("humidity", humidity);       // humidity
	        request.setAttribute("windSpeed", windKph);       // wind speed in kph (double)
	        request.setAttribute("weatherData", responseContent.toString());  // full JSON
	        connection.disconnect();
		 }catch (IOException e) {
	            e.printStackTrace();
	     }
		// Forward the request to the weather.jsp page for rendering
	     request.getRequestDispatcher("index.jsp").forward(request, response);

        
		

	}

}
//https://api.openweathermap.org/data/2.5/forecast/daily?q={city%20name}&cnt={cnt}&appid={API%20key}

//fe795de3bae97f8bb3da04147bb143fa
