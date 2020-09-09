import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;


/**
*
* @author roshansreekanth
*/
public class MainDB {
	
    
    /** Creates a new instance of RunDB */
    public MainDB() {
    }
    
    /**
     * @param args the command line arguments
     */
    
    //-------------------------------------------------------------------------------------------
    // putQuotes() : Puts the '' quotes around a value if it's a String to avoid a syntax error
    //-------------------------------------------------------------------------------------------
    
    public static void putQuotes(ArrayList<Object> myList)
    {
    	for(int i = 0; i < myList.size(); i++)
    	{
    		if(myList.get(i) instanceof String)
    		{
    			if(!(myList.get(i).equals("DEFAULT")))
    			{
	    			Object myObj = myList.get(i);
	    			myObj = "\'" + myObj + "\'";
	    			myList.set(i, myObj);
    			}
    		}
    	}
   
    }
    
    //-------------------------------------------------
    // pressEnter(): Prompt to prevent jerky transition
    //-------------------------------------------------
    public static void pressEnter()
    {
    	Scanner enter = new Scanner(System.in);
    	System.out.println("Press Enter to continue");
    	enter.nextLine();
    }
    
    //----------------------------------------------------------------------------------
    // createCustomer() : Adds a new customer to the database, 
    //					  and makes sure the customer doesn't already exist
    //----------------------------------------------------------------------------------
    public static void createCustomer(Connection con)
    {
    	InsertRecord ir = new InsertRecord();
    	SQLQuery qr = new SQLQuery();
    	
    	ArrayList<Object> customerDetails = new ArrayList<Object>();
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Enter the first name: ");
    	String firstName = sc.nextLine();
    	System.out.println(String.format("Enter the last name of %s: ", firstName));
    	String lastName = sc.nextLine();
    	
    	System.out.println(String.format("Enter the email for %s %s:", firstName, lastName));
    	String email = sc.nextLine();
    	while(qr.ifExists(con, "customer", "email", email))
    	{
    		System.out.println("Email already exists! Enter another one: ");
    		email = sc.nextLine();
    	}
    	System.out.println(String.format("Enter a password associated with %s ", email));
    	String password = sc.nextLine();
    	
    	customerDetails.add("DEFAULT");
    	customerDetails.add(password);
    	customerDetails.add(firstName);
    	customerDetails.add(lastName);
    	customerDetails.add(email);
    	
    	putQuotes(customerDetails);
    	ir.insert(con, "customer", customerDetails);
    	System.out.println("Added customer successfully!");
    	    	
    	pressEnter();
    }
   
    
    //-----------------------------------------------------------------------------------
    // generateRandomSeat(): Assigns a randomly generated seat number to the customer.
    //-----------------------------------------------------------------------------------
    public static String generateRandomSeat(Connection con)
    {
    	String[] alphabets = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"};
    	
    	String seatNo = "";
    	
    	Random rand = new Random();
    	int index = rand.nextInt(alphabets.length);
    	
    	String letterPart = alphabets[index];
    	int numberPart = rand.nextInt(10);
    	seatNo = letterPart + numberPart;
    	
    	SQLQuery qr = new SQLQuery();
    	if(qr.ifExists(con, "ticket", "seatNo", seatNo))
    	{
    		generateRandomSeat(con);
    	}
    	return seatNo;
    }
    
    
    //-----------------------------------------------------------------------------------
    // determinePrice(): Determines the price the customer pays based on date of booking.
    //
    // 1 week before flight : 30% surcharge
    // 2 weeks before flight: 20% surcharge
	// 3 weeks before flight: 10% surcharge	
    //------------------------------------------------------------------------------------
    public static float determinePrice(Connection con, LocalDate bookingDate, LocalDate flightDate, int flightNo)
    {
    	SQLQuery qr  = new SQLQuery();
    	float customerPrice = Float.parseFloat(qr.getValue(con, "basePrice", "flight", "flightNo", flightNo));
    	
    	
    	long daysBetween = ChronoUnit.DAYS.between(bookingDate, flightDate);
    	
    	if(daysBetween <= 7)
    	{
    		customerPrice += customerPrice * 0.3;
    	}
    	else if(daysBetween <= 14)
    	{
    		customerPrice += customerPrice * 0.2;
    	}
    	else if(daysBetween <= 21)
    	{
    		customerPrice += customerPrice * 0.1;
    	}
    	
    	return customerPrice;
    }
    
    //---------------------------------------------------------------------------
    // customerLogin() : Validates customer details before an action is performed
    //---------------------------------------------------------------------------
    
    public static String customerLogin(Connection con)
    {
    	ArrayList<Object> inputDetails = new ArrayList<Object>();
    	inputDetails.add("*");
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Enter the email of the customer: ");
    	String email = sc.nextLine();
    	
    	SQLQuery qr = new SQLQuery();
    	
    	if(qr.ifExists(con, "customer", "email", email))
    	{
    		System.out.println("Enter password for customer:");
    		String password = sc.nextLine();
    		while(! qr.multipleExists(con, "customer", "email", email, "password", password))
    		{
    			System.out.println("Wrong password! Try again: ");
    			password = sc.nextLine();
    		}
    		
    	}
    	else
    	{
    		return null;	
    	}
    	return email;
    }
    
    //-------------------------------------------------------------------------------
    // promptCreateCustomer() : If a customer doesn't exist, a new one can sign up
    //-------------------------------------------------------------------------------
    public static void promptCreateCustomer(Connection con)
    {
    	Scanner kb = new Scanner(System.in);
		System.out.println("Customer doesn't exist. Would you like to sign up? (y/n)");
		String answer = kb.nextLine();
		
		if(answer.equals("y"))
		{
			createCustomer(con);
			
		}
		else
		{
			pressEnter();
			return;
		}	    	
    }
    
    //-----------------------------------------------
    // showMenu() : Displays the main menu
    //-----------------------------------------------
    public static void showMenu()
    {
    	System.out.println("CUSTOMER PERSPECTIVE");
    	System.out.println("-----------------------------------");
    	System.out.println("1. BOOK A TICKET");
    	System.out.println("2. CANCEL LATEST TICKET");
    	System.out.println();
    	System.out.println("ADMINISTRATOR PERSPECTIVE");
    	System.out.println("-----------------------------------");
    	System.out.println("3. ADD A NEW FLIGHT");
    	System.out.println("4. VIEW ALL FLIGHTS");
    	System.out.println("5. CHANGE FLIGHT DETAILS");
    	System.out.println("6. CANCEL A FLIGHT");
    	System.out.println("7. EXIT");
    }
    
    //----------------------------------------------------------------
    // bookTicket(): Gets details, validates, and books a new ticket.
    //-----------------------------------------------------------------
    public static void bookTicket(Connection con)
    {
    
	    	String email = customerLogin(con);
	    	
	    	if(email != null)
	    	{
	    		Scanner sc = new Scanner(System.in);
	    		SQLQuery qr = new SQLQuery();
	    		
				int customerId = Integer.parseInt(qr.getValue(con, "customerId", "customer", "email", email));
				System.out.println("Enter your passport no: ");
				String passportNo = sc.nextLine();
				
				System.out.println("You are about to enter the Flight No. Would you like to view the available flights first? (y/n)");
				String choice = sc.nextLine();
				
				if(choice.equals("y"))
				{
					ArrayList<Object> flightViewDetails = new ArrayList<Object>();
					flightViewDetails.add("flightNo");
					flightViewDetails.add("carrierName");
					flightViewDetails.add("portOfCall");
					flightViewDetails.add("portOfDestination");
					flightViewDetails.add("travelDate");
					qr.queryStatement(con, "flight", flightViewDetails);
					System.out.println();
				}
	    			
	    		System.out.println("Enter the flight number for your desired flight: ");
	    		int flightNo = sc.nextInt();
	    		
	    		while(!qr.ifExists(con, "flight", "flightNo", flightNo))
	    		{
	    			System.out.println("Wrong flight number. Please try again: ");
	    			flightNo = sc.nextInt();
	    		}
	    		
	    		String seatNo = generateRandomSeat(con);
	    		
	    		Scanner details = new Scanner(System.in);
	    		
	    		System.out.println("Seat Number: " + seatNo);
	    		
	    		System.out.println("Enter the date of booking (yy-mm-dd): ");
	    		
	    		String stringBookingDate = details.nextLine();
	        	String stringFlightDate = qr.getValue(con, "travelDate", "flight", "flightNo", flightNo);
	        	
	        	LocalDate bookingDate = LocalDate.parse(stringBookingDate);
	        	LocalDate flightDate = LocalDate.parse(stringFlightDate);
	        	
	        	while(bookingDate.compareTo(flightDate) > 0)
	        	{
	        		System.out.println("Error! Booking date can't be later than flight date. Try again: ");
		    		stringBookingDate = details.nextLine();
		        	bookingDate = LocalDate.parse(stringBookingDate);
	        	}
	        	
	        	float customerPrice = determinePrice(con, bookingDate, flightDate, flightNo);
	        	
	        	InsertRecord ticketDetails = new InsertRecord();
	        	
	        	ArrayList<Object> insertDetails = new ArrayList<Object>();
	        	insertDetails.add("DEFAULT");
	        	insertDetails.add(customerId);
	        	insertDetails.add(flightNo);
	        	insertDetails.add(passportNo);
	        	insertDetails.add(customerPrice);
	        	insertDetails.add(seatNo);
	        	insertDetails.add(stringBookingDate);
	        	putQuotes(insertDetails);
	        	ticketDetails.insert(con, "ticket", insertDetails);
	        	
	        	System.out.println("Your flight costs: " + customerPrice);
	        	pressEnter();
	        }
    		
	    	else
	    	{
	    	
	    		promptCreateCustomer(con);
	    	}
    }
    
    //----------------------------------------------------------------
    // bookTicket(): If booking exists, cancels the latest one.
    //-----------------------------------------------------------------
    public static void cancelTicket(Connection con)
    {
    	Scanner sc = new Scanner(System.in);
    	String email = customerLogin(con);
    	
    	
    	if(email != null)
    	{
	    	SQLQuery qr = new SQLQuery();
	    	
    		int customerId = Integer.parseInt(qr.getValue(con, "customerId", "customer", "email", email));
    		String bookingNo = qr.getValue(con, "bookingNo", "ticket", "customerId", customerId);
    		
    		if(bookingNo == null)
    		{
    			System.out.println("Customer does not have a booking");
    			pressEnter();
    		}
    		else
    		{
    			System.out.println("Your booking is: ");
    			ArrayList<Object>  ticketDetails = new ArrayList<Object>();
    			qr.conditionalQuery(con, "ticket", "bookingNo", bookingNo);
    			System.out.println("Are you sure you want to cancel? (y/n):");
    			String choice = sc.nextLine();
    			if(choice.equals("y"))
    			{
    				DeleteRecord del = new DeleteRecord();
    				del.delete(con, "ticket", "bookingNo", bookingNo);
    			}
    		}
    	}
    	
    	else
    	{
    		promptCreateCustomer(con);
    	}
    }
    
    //----------------------------------------------------------
    // addFlight(): Adds a new flight and validates the details
    //----------------------------------------------------------
    public static void addFlight(Connection con)
    {
    	SQLQuery qr = new SQLQuery();
    	ArrayList<Object> selectAll = new ArrayList<Object>();
    	selectAll.add("*");
    	qr.queryStatement(con, "carrier", selectAll);
    	
    	Scanner kb = new Scanner(System.in);

    	System.out.println("Select a carrier: ");
    	String carrier = kb.nextLine();
    	
    	while(!qr.ifExists(con, "carrier", "carrierName", carrier))
    	{
    		System.out.println("This carrier is not in our database. Try another one: ");
    		carrier = kb.nextLine();
    	}
    	
    	qr.queryStatement(con, "airport", selectAll);
    	System.out.println("Select the starting airport code (eg. ORK, CDG): ");
    	String callPort = kb.nextLine();
    	while(!qr.ifExists(con, "airport", "airportCode", callPort))
    	{
    		System.out.println("This code is invalid. Try another one: ");
    		callPort = kb.nextLine();
    	}
    	
    	System.out.println("Enter a destination port code: ");
    	String destinationPort = kb.nextLine();
    	while(!qr.ifExists(con, "airport", "airportCode", destinationPort))
    	{
    		System.out.println("This code is invalid. Try another one: ");
    		destinationPort = kb.nextLine();
    	}
    	
    	while(callPort.equals(destinationPort))
    	{
    		System.out.println("Error! Source and destination port cannot be the same.");
    		pressEnter();
    		return;
    	}
    	
    	System.out.println("Enter the arrival time to the airport (hh:mm): ");
    	String arrivalTime = kb.nextLine();
    	
    	System.out.println("Enter the departure time from the airport (hh:mm): ");
    	String departureTime = kb.nextLine();
    	
    	LocalTime arrivalObject = LocalTime.parse(arrivalTime);
    	LocalTime departureObject = LocalTime.parse(departureTime);
    	
    	while (arrivalObject.isAfter(departureObject))
    	{
    		System.out.println("Arrival time cannot be after Departure time. Please re-enter arrival time (hh:mm):");
    		arrivalTime = kb.nextLine();
    		arrivalObject = LocalTime.parse(arrivalTime);
    	}
    	
    	System.out.println("Enter the travel date for the flight (yy-mm-dd): ");
    	String travelDate = kb.nextLine();
    	
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Enter the cost of the flight (base price)");
    	float basePrice = sc.nextFloat();
    	
    	while(basePrice < 0)
    	{
    		System.out.println("Base price can't be negative. Please enter again: ");
    		basePrice = sc.nextFloat();
    	}
    	
    	ArrayList<Object> flightDetails = new ArrayList<Object>();
    	
    	flightDetails.add("DEFAULT");
    	flightDetails.add(carrier);
    	flightDetails.add(callPort);
    	flightDetails.add(destinationPort);
    	flightDetails.add(arrivalTime);
    	flightDetails.add(departureTime);
    	flightDetails.add(travelDate);
    	flightDetails.add(basePrice);
    	putQuotes(flightDetails);
    	
    	InsertRecord ir = new InsertRecord();
    	ir.insert(con, "flight", flightDetails);
    	
    	pressEnter();
    }
    
    //-----------------------------------------------
    // viewFlights() : Displays current flights
    //-----------------------------------------------
    public static void viewFlights(Connection con)
    {
    	SQLQuery qr = new SQLQuery();
    	ArrayList<Object> viewAll = new ArrayList<Object>();
    	viewAll.add("*");
    	qr.queryStatement(con, "flight", viewAll);
    	
    	pressEnter();
    }
    
    //--------------------------------------------------------
    // changeDetails(): Changes and validates flight details
    //--------------------------------------------------------
    public static void changeDetails(Connection con)
    {
    	viewFlights(con);
    	
    	Scanner sc = new Scanner(System.in);
    	UpdateRecord uq = new UpdateRecord();
    	SQLQuery qr = new SQLQuery();
    	
    	System.out.println("Select a flight number to change it's details:");
    	
    	int flightNo = Integer.parseInt(sc.nextLine());
    	
    	while(!qr.ifExists(con, "flight", "flightNo", flightNo))
    	{
    		System.out.println("Invalid flight number. Try again: ");
    		flightNo = Integer.parseInt(sc.nextLine());
    	}
    	
    	String arrivalTime = qr.getValue(con, "arrivalTime", "flight", "flightNo", flightNo);
    	String departureTime = qr.getValue(con, "departureTime", "flight", "flightNo", flightNo);

    	LocalTime arrivalObject = LocalTime.parse(arrivalTime);
    	LocalTime departureObject = LocalTime.parse(departureTime);
    	
    	System.out.println("1. CHANGE PRICE\n2. CHANGE ARRIVAL TIME\n3. CHANGE DEPARTURE TIME");
    	System.out.println("Enter your choice: ");
    	int choice = Integer.parseInt(sc.nextLine());
    	
    	while(choice > 3 || choice < 1)
    	{
    		System.out.println("Invalid choice. Try again: ");
    		choice = Integer.parseInt(sc.nextLine());
    	}
    	
    	switch(choice)
    	{
    		case 1:
    			System.out.println("Enter the new price: ");
    			float newPrice = Float.parseFloat(sc.nextLine());
    			
    			while(newPrice < 0)
    			{
    				System.out.println("Invalid price. Try again: ");
        			newPrice = Float.parseFloat(sc.nextLine());
    			}
    			
    			UpdateRecord changePrice = new UpdateRecord();
    			changePrice.update(con, "flight", "basePrice", newPrice, "flightNo", flightNo);
    			System.out.println("Updated price");
    			break;
    			
    		case 2:
    			System.out.println("Enter the new arrival time: ");
    			arrivalTime = sc.nextLine();
    			arrivalObject = LocalTime.parse(arrivalTime);
    			while(arrivalObject.isAfter(departureObject))
    			{
    				System.out.println("Arrival time can't be after departure time. Try again: ");
    				arrivalTime = sc.nextLine();
        			arrivalObject = LocalTime.parse(arrivalTime);
    			}
    			UpdateRecord changeArrival = new UpdateRecord();
    			changeArrival.update(con, "flight", "arrivalTime", arrivalTime, "flightNo", flightNo);
    			System.out.println("Arrival updated");
    			break;
    			
    		case 3:
    			System.out.println("Enter the new departure time: ");
    			departureTime = sc.nextLine();
    			departureObject = LocalTime.parse(departureTime);
    			while(departureObject.isBefore(arrivalObject))
    			{
    				System.out.println("Departure time can't be before arrival time. Try again: ");
    				departureTime = sc.nextLine();
        			departureObject = LocalTime.parse(departureTime);
    			}
    			UpdateRecord changeDeparture = new UpdateRecord();
    			changeDeparture.update(con, "flight", "departureTime", departureTime, "flightNo", flightNo);
    			System.out.println("Departure updated");
    	}
    	pressEnter();
    }
    
    //----------------------------------------------------------------
    // cancelFlight(): Cancels a flight and it's associated tickets
    //----------------------------------------------------------------
    public static void cancelFlight(Connection con)
    {
    	viewFlights(con);
    	
    	SQLQuery qr = new SQLQuery();
    	Scanner sc = new Scanner(System.in);
		System.out.println("Select a flight number to cancel it (all tickets will be cancelled too):");
		    	
		    	int flightNo = Integer.parseInt(sc.nextLine());
		    	
		    	while(!qr.ifExists(con, "flight", "flightNo", flightNo))
		    	{
		    		System.out.println("Invalid flight number. Try again: ");
		    		flightNo = Integer.parseInt(sc.nextLine());
		    	}
		    	
		    	System.out.println("Are you sure you want to cancel the flight? (y/n)");
		    	String choice = sc.nextLine();
		    	
		    	if(choice.equals("y"))
		    	{
			    	DeleteRecord del = new DeleteRecord();
			    	del.delete(con, "ticket", "flightNo", flightNo);
			    	del.delete(con, "flight", "flightNo", flightNo);
		    	}
		    	pressEnter();
    }
    
    
    //---------------------------------------------------------------
    // processChoice(): Takes the user to the respective function.
    //---------------------------------------------------------------
    public static void processChoice(Connection con, int choice)
    {
    	
    	if(choice > 2)
    	{
    		Scanner sc = new Scanner(System.in);
    		SQLQuery qr = new SQLQuery();
    		//email: root@domain.com password: root
    		
    		System.out.println("Enter administrator password: ");
    		String adminPassword = sc.nextLine();
    		
    		if(!qr.ifExists(con, "customer", "password", adminPassword))
    		{
    			System.out.println("Wrong details");
    			pressEnter();
    			return;
    		}
    	}
    	switch(choice)
    	{
    		case 1: bookTicket(con); break;
    		
    		case 2: cancelTicket(con); break; 
    		
    		case 3: addFlight(con); break;
    		
    		case 4: viewFlights(con); break;
    		
    		case 5: changeDetails(con); break;	
    		
    		case 6: cancelFlight(con); break;
    	}
    }

    
    public static void main(String[] args) {
        try {
	
	        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	        	        
	        // You Must replace the details with your name and your password with your password in the driver url 
	        
	        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/r00170592?user=root&password=root");
	        System.out.println ("Database connection established");
	        Statement s = con.createStatement ();
	        showMenu();
	        Scanner sc = new Scanner(System.in);
	        System.out.println("Enter your choice: ");
	        
	        int choice = sc.nextInt();
	        while(choice != 7)
	        {
	        	processChoice(con, choice);
	        	showMenu();
	        	System.out.println("Enter your choice: ");
	        	choice = sc.nextInt();
	        }
	        
	        System.out.println("Thank you for using the program!");
	        
         }catch (Exception ex) {
            System.out.println("SQLException: " + ex.getMessage());
            
        };
    
    }
    
}
