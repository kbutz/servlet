
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <%--<link rel="stylesheet" href="./static/css/site.css" />--%>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="./static/css/bootstrap.min.css" />
    <title>Vehicle Management</title>

</head>
<body style="max-width:1200px;">
<div class="container">
    <div class="row">
    <div class="col-md-12">
        <h2>Vehicle Management</h2>
        <h5>Form #1: chooseVehicle</h5>
      <form name="chooseVehicle" action="./vehicle" method="post">
          <div class="row form-group">
          <input type="hidden" name="formName" value="chooseVehicle"/>
          <select required name="selectVehicleMake" onchange="submit()">
            <option value="0">(Select Make)</option>

            <c:forEach var="vehicleMake" items="${vehicleMakeList}">
                <c:choose>
                    <c:when test="${vehicleMake.vehicleMakeId == vehicleMakeId}">
                        <option selected value="${vehicleMake.vehicleMakeId}">${vehicleMake.vehicleMakeName}</option>
                    </c:when>
                    <c:otherwise>
                        <option value="${vehicleMake.vehicleMakeId}">${vehicleMake.vehicleMakeName}</option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            </select>
          </div>
      </form>
      <form name="addVehicle" action="./vehicle" method="post">
          <div class="row form-group">
        <select required name="selectVehicleModel">
            <c:if test="${empty vehicleModelList}">
                <option value="0">(Please Select Model)</option>
            </c:if>
            <c:forEach var="vehicleModel" items="${vehicleModelList}">
                <c:choose>
                    <c:when test="${vehicleModel.vehicleModelId == vehicleModelId}">
                        <option selected value="${vehicleModel.vehicleModelId}">${vehicleModel.vehicleModelName}</option>
                    </c:when>
                    <c:otherwise>
                        <option value="${vehicleModel.vehicleModelId}">${vehicleModel.vehicleModelName}</option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </select>
        </div>
       <input type="hidden" name="formName" value="addVehicle"/>
       <input type="hidden" name="vehicleMakeId" value="${vehicleMakeId}" />
       <input type="hidden" name="vehicleModelId" value="${vehicleModelId}" />
       <div class="row form-group">
            <input required type="text" name="licensePlate" class="form-control" value="${licensePlate}" placeholder="License Plate" />
       </div>
       <div class="row form-group">
            <input required type="text" name="VIN" class="form-control" value="${VIN}" placeholder="VIN" />
       </div>
       <div class="row form-group">
            <input required type="number" name="vehicleYear" class="form-control" value="${vehicleYear}" placeholder="Year" />
       </div>
       <div class="row form-group">
            <input required type="text" name="color" class="form-control" value="${color}" placeholder="Color" />
       </div>
       <button type="submit" class="btn btn-primary">Add Vehicle</button>
   </form>
   </div> <!-- end #border for add vehicle form 1-->
</div> <!-- end div row -->
</div>
<div class="container" style="width:1200px;">
   <h5>Form #2: updateVehicle</h5>
<div class="alert alert-success alert-dismissible">
    <button type="button" class="close" data-dismiss="alert" >x</button>${loadSuccessful}${addSuccessful} ${updateSuccessful} ${deleteSuccessful}
</div>
  <div class="row">
   <div class="col-lg-8">
        <c:if test="${empty vehicleList}">
          No vehicles to manage.
        </c:if>
       <table class="table table-hover">
           <thead class="thead-inverse">
           <tr>
               <th>Vehicle Id</th>
               <th>License Plate</th>
               <th>VIN</th>
               <th>Year</th>
               <th>Color</th>
               <th>Make</th>
               <th>Model</th>
               <th>Update</th>
               <th>Delete</th>
           </tr>
           </thead>
           <tbody>
        <c:forEach var="vehicle" items="${vehicleList}">
            <form name="updateVehicle" action="./vehicle" method="post">
               <input type="hidden" name="formName" value="updateVehicle"/>
                <input type="hidden" name="updatedVehicleId" value="${vehicle.vehicleId}" />
               <tr>
                   <!-- todo: add input fields for below fields -->
               <td>${vehicle.vehicleId}</td>
               <td><input type="text" name="licensePlate" value="${vehicle.licensePlate}"/></td>
               <td><input type="text" name="VIN" value="${vehicle.vin}"/></td>
               <td><input type="number" name="vehicleYear" value="${vehicle.year}" /></td>
               <td><input type="text" name="color" value="${vehicle.color}" /></td>
               <td>
                   <select name="vehicleMakeId" onchange="submit()">
                       <option value="0">(Select Make)</option>
                       <c:forEach var="updateVehicleMake" items="${vehicleMakeList}">
                           <c:choose>
                               <c:when test="${updateVehicleMake.vehicleMakeId == vehicle.vehicleMakeAndModel.vehicleMakeId}">
                                   <option selected value="${updateVehicleMake.vehicleMakeId}">${updateVehicleMake.vehicleMakeName}</option>
                               </c:when>
                               <c:otherwise>
                                   <option value="${updateVehicleMake.vehicleMakeId}">${updateVehicleMake.vehicleMakeName}</option>
                               </c:otherwise>
                           </c:choose>
                       </c:forEach>
                   </select>
               </td>
               <td>
                   <select name="selectVehicleModel">
                   <c:forEach var="vehicleModelList" items="${vehicleModelMapList}" varStatus="vehicleModelListStatus">
                       <c:choose>
                           <c:when test="${vehicleModelList.key.vehicleMakeId == vehicle.vehicleMakeAndModel.vehicleMakeId}">
                               <c:forEach var="vehicleModel" items="${vehicleModelList.value}" varStatus="vehicleModelStatus">
                                   <c:choose>
                                       <c:when test="${vehicleModel.vehicleModelId == vehicle.vehicleMakeAndModel.vehicleModelId}">
                                           <option selected value="${vehicleModel.vehicleModelId}">${vehicleModel.vehicleModelName}</option>
                                       </c:when>
                                       <c:otherwise>
                                           <option value="${vehicleModel.vehicleModelId}">${vehicleModel.vehicleModelName}</option>
                                       </c:otherwise>
                                   </c:choose>
                               </c:forEach>
                           </c:when>
                       </c:choose>
                   </c:forEach>
                   </select>
               </td>
               <td><button type="submit" name="update" value="update">Update</button></td>
               <td><button type="submit" name="update" value="delete">Delete</button></td>
            </tr>
            </form>
       </c:forEach>
           </tbody>
       </table>
   </div>
  </div>
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.3.2/js/tether.min.js"></script>
<script src="./static/js/bootstrap.min.js"></script>
</body>
</html>
