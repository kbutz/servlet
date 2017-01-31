package com.astontech.servlet;

import com.astontech.bo.Vehicle;
import com.astontech.bo.VehicleMake;
import com.astontech.bo.VehicleModel;
import com.astontech.dao.VehicleDAO;
import com.astontech.dao.mysql.MySQL;
import com.astontech.dao.mysql.VehicleDAOImpl;
import common.helpers.ServletHelper;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kylebutz1 on 11/10/2016.
 */
@WebServlet(name = "VehicleServlet")
public class VehicleServlet extends HttpServlet {

    final static Logger logger = Logger.getLogger((VehicleServlet.class));

    private static VehicleDAO vehicleDAO = new VehicleDAOImpl();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // initialized Vehicle object to persist updateVehicle's cascading dropdown menus
        Vehicle vehicle = new Vehicle();
        switch(request.getParameter("formName")) {

            case "chooseVehicle":
                // updates addVehicle's cascading dropdown menus
                chooseModelList(request);
                break;
            case "addVehicle":
                // using the already created Vehicle object for the addVehicle method
                addVehicle(request, vehicle);
                break;
            case "updateVehicle":
                // updates cascading dropdown menus for vehicleList, updates vehicles in DB, and deletes vehicles from DB
                vehicle = updateVehicle(request, vehicle);
                break;
            default:
                break;
        }

        setPostRequestAttributes(request, vehicle);
        request.getRequestDispatcher("./vehicle.jsp").forward(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setDoRequestAttributes(request);
        request.getRequestDispatcher("./vehicle.jsp").forward(request,response);
    }

    // both setRequestAttribute functions populate the vehicleMake list, then loop over the make list populating
    // map of <Make, List<Model>>
    // requestAttributes for Post request may contain param updatedVehicleId
    // Here we check for the updated vehicle and add it to our vehicle list param without adding to the db.
    // finally the full vehicle list is passed to the vehicleList attribute
    private static void setPostRequestAttributes(HttpServletRequest request, Vehicle vehicle){
        List<VehicleMake> vehicleMakeList = vehicleDAO.getVehicleMakeList();
        request.setAttribute("vehicleMakeList", vehicleMakeList);
        Map<VehicleMake, List<VehicleModel>> map = new HashMap<>();
        for (VehicleMake v : vehicleMakeList) {
            List<VehicleModel> vehicleModelList = vehicleDAO.getVehicleModelListByMakeId(v.getVehicleMakeId());
            map.put(v, vehicleModelList);
        }
        request.setAttribute("vehicleModelMapList", map);
        List<Vehicle> vehicleList = vehicleDAO.getVehicleList();
        // checks if updatedVehicleId has been populated, then replaces the vehicleList attribute
        // with the updated vehicle without added to db.
        if (request.getParameter("updatedVehicleId") != null){
            for (Vehicle v : vehicleList){
                if (v.getVehicleId() == vehicle.getVehicleId()){
                    vehicleList.set(vehicleList.indexOf(v), vehicle);
                }
            }
        }
        request.setAttribute("loadSuccessful", "Found " + vehicleList.size() + " vehicle records.  ");
        request.setAttribute("vehicleList", vehicleList);
    }

    private static void setDoRequestAttributes(HttpServletRequest request){
        List<VehicleMake> vehicleMakeList = vehicleDAO.getVehicleMakeList();
        request.setAttribute("vehicleMakeList", vehicleMakeList);
        Map<VehicleMake, List<VehicleModel>> map = new HashMap<>();
        for (VehicleMake v : vehicleMakeList) {
            List<VehicleModel> vehicleModelList = vehicleDAO.getVehicleModelListByMakeId(v.getVehicleMakeId());
            map.put(v, vehicleModelList);
        }
        request.setAttribute("vehicleModelMapList", map);
        List<Vehicle> vehicleList = vehicleDAO.getVehicleList();
        request.setAttribute("vehicleList", vehicleList);
        request.setAttribute("loadSuccessful", "Found " + vehicleList.size() + " vehicle records.  ");
    }

    private static void chooseModelList(HttpServletRequest request) {
        logger.info("Form #1 - Form Name= " + request.getParameter("formName"));
        ServletHelper.logRequestParams(request);

        //notes:  everything comes back from the request as a STRING
        String selectedMakeId = request.getParameter("selectVehicleMake");
        // if a Make has been chosen, chooseModelList is submitted, setting the attributes with the related
        // vehicleModelList via DAO call and persisting the vehicleMakeId back to the related attribute
        request.setAttribute("vehicleModelList", vehicleDAO.getVehicleModelListByMakeId(Integer.parseInt(selectedMakeId)));
        request.setAttribute("vehicleMakeId", selectedMakeId);
    }

    private static void addVehicle(HttpServletRequest request, Vehicle addedVehicle) {
        logger.info("Form #2 - Form Name= " + request.getParameter("formName"));
        ServletHelper.logRequestParams(request);
        // save vehicleMakeId to populate associated modelList, which is not persisted by default
        String selectedMakeId = request.getParameter("vehicleMakeId");

        requestToVehicle(request, addedVehicle);
        int vehicleId = vehicleDAO.insertVehicle(addedVehicle);
        if (vehicleId > 0) {
            request.setAttribute("addSuccessful", "Vehicle inserted in Database Successfully  ");
        } else {
            request.setAttribute("addSuccessful", "Vehicle insert FAILED  ");
        }
        vehicleToRequest(request, addedVehicle);

        //persists vehicleModelList - initially loads with no vehicleModelList until make is chosen
        request.setAttribute("vehicleModelList", vehicleDAO.getVehicleModelListByMakeId(Integer.parseInt(selectedMakeId)));
    }

    private static Vehicle updateVehicle(HttpServletRequest request, Vehicle vehicle) {
        logger.info("Form #3 - Form Name= " + request.getParameter("formName") + ", " + request.getParameter("updatedVehicleId"));
        ServletHelper.logRequestParams(request);

        requestToVehicle(request, vehicle);
        // checks if param for update is not null and set to 'update', the uses DAO call to update
        if (request.getParameter("update") != null && request.getParameter("update").equals("update")){
            if (vehicleDAO.updateVehicle(vehicle)){
                request.setAttribute("updateSuccessful", "Vehicle ID#" + vehicle.getVehicleId() + " successfully updated.    ");
            }
        // if update paramater is not null and set to delete, vehicle deleted by ID
        } else if (request.getParameter("update") != null && request.getParameter("update").equals("delete")){
            System.out.println("The delete button was clicked.");
            if (vehicleDAO.deleteVehicle(vehicle.getVehicleId())){
                request.setAttribute("deleteSuccessful", "Vehicle ID#" + vehicle.getVehicleId() + " successfully deleted.    ");
            }
            vehicle.setVehicleId(0);
        }
        return vehicle;
    }

    private static void requestToVehicle(HttpServletRequest request, Vehicle vehicle) {
        //notes:    everything comes back from the request as a string
        // updatedVehicleId will be not null or not 0 if, in the updateVehicle form,
        // a make dropdown menu has been changed, or a vehicle has been updated in the db
        // otherwise, the vehicleId is pulled from the DB in getVehicle or getVehicleList
        if (request.getParameter("updatedVehicleId") != null && Integer.parseInt(request.getParameter("updatedVehicleId")) != 0){
            System.out.println("UpdatedVehicleId: " + request.getParameter("updatedVehicleId"));
            vehicle.setVehicleId(Integer.parseInt(request.getParameter("updatedVehicleId")));
        }
        vehicle.getVehicleMakeAndModel().setVehicleMakeId(Integer.parseInt(request.getParameter("vehicleMakeId")));
        vehicle.getVehicleMakeAndModel().setVehicleModelId(Integer.parseInt(request.getParameter("selectVehicleModel")));
        vehicle.setLicensePlate(request.getParameter("licensePlate"));
        vehicle.setYear(Integer.parseInt(request.getParameter("vehicleYear")));
        vehicle.setVin(request.getParameter("VIN"));
        vehicle.setColor(request.getParameter("color"));

    }

    private static void vehicleToRequest(HttpServletRequest request, Vehicle vehicle) {
        request.setAttribute("vehicleMakeId", vehicle.getVehicleMakeAndModel().getVehicleMakeId());
        request.setAttribute("vehicleModelId", vehicle.getVehicleMakeAndModel().getVehicleModelId());
        request.setAttribute("licensePlate", vehicle.getLicensePlate());
        request.setAttribute("VIN", vehicle.getVin());
        request.setAttribute("vehicleYear", vehicle.getYear());
        request.setAttribute("color", vehicle.getColor());
    }

}
