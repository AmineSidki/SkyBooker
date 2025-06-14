package skybooker.client.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import skybooker.client.DTO.*;
import skybooker.client.requests.Client;
import skybooker.client.requests.ClientCache;
import skybooker.client.utils.GeneralUtils;

import java.util.List;

public class SearchResultsController {

    private static Long departureFlight;
    private static Long arrivalFlight;
    private static DatePicker departureDate;

    @FXML
    private VBox searchResultsContainer;

    @FXML
    protected void initialize() {
        // fetching the vols
        List<VolDTO> vols;
        ObjectMapper mapper = new ObjectMapper();
        try {
            String res = Client.get("/vol/getFromVilles/" + departureFlight + "/" + arrivalFlight);
            vols = mapper.readValue(res, new TypeReference<List<VolDTO>>(){});
            for (VolDTO v : vols) {
                // this is building on the way @Amine did it in the past
                // Note that this can't throw an exception (I guess...)
                // Here I'm fetching the values and putting them in variables
                // so that if we want to change the layout later we don't have to rewrite
                // the fetching logic
                AvionDTO avion = ClientCache.get(v.getAvionId(), AvionDTO.class);
                CompanieAerienneDTO companieAerienne = ClientCache.get(avion.getCompanieAerienneId(), CompanieAerienneDTO.class);
                AeroportDTO aeroportDepart = ClientCache.get(v.getAeroportDepartId(), AeroportDTO.class);
                AeroportDTO aeroportArrive = ClientCache.get(v.getAeroportArriveId(), AeroportDTO.class);

                // Here's the layout, change here ;; Wow , who do you take me for , YOU ?

                StackPane stackPane = new StackPane();
                HBox globalContainer = new HBox();
                globalContainer.setAlignment(Pos.CENTER);
                globalContainer.setSpacing(50);
                globalContainer.setMinWidth(480);
                globalContainer.setMaxWidth(480);

                Text airline = new Text(companieAerienne.getNom()) ;
                airline.setFont(new Font("Roboto" , 15));

                HBox container = new HBox();
                container.setAlignment(Pos.CENTER);
                container.setSpacing(20);

                Text depAirport = new Text(aeroportDepart.getIataCode()) ;
                depAirport.setFont(new Font("Roboto" , 15));

                Text depTime = new Text(v.getHeureDepart().toString()) ;
                depTime.setFont(new Font("Roboto" , 15));

                Text arrTime = new Text(v.getHeureArrive().toString()) ;
                arrTime.setFont(new Font("Roboto" , 15));

                Text arrAirport = new Text(aeroportArrive.getIataCode()) ;
                arrAirport.setFont(new Font("Roboto" , 15));

                container.getChildren().addAll(depAirport , depTime , arrAirport , arrTime );

                res = Client.get("/vol/price/" + v.getId() + "/" + 1); // Economy class
                PriceDTO priceDTO = mapper.readValue(res, new TypeReference<>() {});
                Text price = new Text((priceDTO.getPrice().toString())) ;
                price.setFont(new Font("Roboto" , 15));

                globalContainer.getChildren().addAll(airline , container , price );

                Button button = new Button();
                button.setOpacity(0);
                button.setMinHeight(50);
                button.setMaxHeight(50);
                button.setMinWidth(480);
                button.setMaxWidth(480);
                button.setStyle("-fx-cursor: hand");
                button.setOnAction(_ -> loadFlightInfo(v.getId()));

                stackPane.getChildren().addAll(globalContainer , button);
                searchResultsContainer.getChildren().addAll(stackPane , new Separator());

            }
        } catch (Exception e) {
            /*
             TODO : here we should redirect the user to a page that tells them
             that we're out of service for now, since that's the only reason for
             an exception to show up here, maybe we can add another route
             that checks the health of the backend
             */
            e.printStackTrace();
            return;
        }
    }

    @FXML
    protected void onReturnButton() {
        GeneralUtils.loadLandingPage();
    }

    @FXML
    protected void onFlightSelected()
    {
        loadFlightInfo(null);
    }

    private void loadFlightInfo(Long id){
        FlightInfoController.setVolId(id);
        GeneralUtils.changeView("flightinfo-view.fxml");
    }

    public static void setDepartureFlight(Long departureFlight) {
        SearchResultsController.departureFlight = departureFlight;
    }

    public static void setArrivalFlight(Long arrivalFlight) {
        SearchResultsController.arrivalFlight = arrivalFlight;
    }

    public static void setDepartureDate(DatePicker departureDate) {
        SearchResultsController.departureDate = departureDate;
    }
}
