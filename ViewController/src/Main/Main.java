package Main;

import ice.util.unit.Unit;

import java.sql.CallableStatement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.services.AppModuleImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputDate;
import oracle.adf.view.rich.component.rich.input.RichInputListOfValues;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.input.RichSelectBooleanCheckbox;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;
import oracle.adf.view.rich.component.rich.output.RichOutputText;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.adf.view.rich.util.ResetUtils;

import oracle.jbo.ApplicationModule;
import oracle.jbo.JboException;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import oracle.jbo.server.ViewObjectImpl;

import oracle.jdbc.OracleTypes;

import java.util.Calendar;

import java.util.Date;

import oracle.sqlj.runtime.Oracle;

public class Main {
    private RichInputText productIDInputTextBind;
    private RichInputText warrantyMonthsInputTextBind;
    private RichInputDate warrantyExpiryDateInputDateBind;
    private RichInputText warrantyRemainingDaysInputTextBind;
    private RichInputText fixedAssetTagInputTextBind;
    private RichInputText productLocationInputTextBind;
    private RichInputText productUserInputTextBind;
    private RichPanelGroupLayout fillInfoPanelGroupLayoutBinding;
    private RichInputListOfValues locationLOVBinding;
    private RichInputListOfValues userLOVBinding;
    private RichTable mnjWarrantyHeaderTable;
    private RichTable warrantyGRNTable;
    private RichSelectOneChoice warrantyTypeLOVBinding;
    private RichPopup savePopUpBinding;
    private RichOutputText grNVOEstimatedRowBinding;
    private RichOutputText warrantyHeaderVOEstimatedRowBinding;
    private String warrantyType;
    private RichInputListOfValues majorCategoryBinding;
    private RichInputListOfValues subCategoryBinding;
    private RichInputText warrantyTagNumberBinding;
    private RichOutputText operatingUnitBinding;
    private RichOutputText grnNumberBinding;
    private RichOutputText grnQtyBinding;
    private String orgName, majorCat, minorCat, tagYear;
    private RichOutputText organizationNameBinding;
    private RichInputText productSerialNumberBinding;
    private RichInputListOfValues subLocationBinding;
    private RichTable dataLoaderTable;
    private RichInputListOfValues fixedAssetTagLOV;
    private RichOutputText poLineIdBindingOuttext;
    private RichInputText prodSrNumDataLoadBinding;
    private RichInputListOfValues fixedTagDataLoadBinding;
    private RichInputListOfValues prodLocationDataLoadBinding;
    private RichInputListOfValues prodUserDataLoadBinding;
    private RichInputListOfValues prodSubLocDataLoadBinding;
    
    private Row[] row;
    
    private int voLength; 
    
    private String allOK;
    private RichInputListOfValues fixedAssetTagHeaderInputLOV;
    private RichSelectBooleanCheckbox setParentItemPopUpCheckBox;
    private RichTable warrantyLineTable;
    private RichInputText parentItemInputFieldBinding;
    private RichOutputText parentWarrantyTagInputBind;
    private RichOutputText itemIdBinding;
    private RichInputListOfValues fixedAssetTagLineLOV;
    private RichOutputText warrantyPeriodInMonthsBind;
    private RichInputText warrantyMonthsInputText2;
    private RichSelectBooleanCheckbox maintainFlag;
    private RichInputText maintainPeriod;
    private RichInputText maintainPeriodCapture;
    private RichInputDate nextMaintainDate;
    private RichInputText maintainPeriodLine;
    private RichInputText maintainPeriodHeader;
    private RichInputDate warrantyStartDate;
    private RichInputDate warrantyExpiryDate;
    private RichOutputText grnDate;
    private String warrantyStartDateC, warrantyExpiryDateC;
    private RichInputDate warrStrtDateDataLoader;
    private RichInputText warrPeriodMonthsDataLoader;
    private RichInputDate warrExpiryDateDataLoader;
    private RichPopup fillWarrantyInfoPopUp;
    private RichInputDate warrStartDateHeader;
    private RichInputText warrPeriodHeader;
    private RichInputDate warrExpiryDateHeader;
    private RichInputDate warrStartDateLine;
    private RichInputText warrPeriodLine;
    private RichOutputText warrExpiryDateLine;

    public Main() {
    }
    
    public ApplicationModule getAppM() {
        DCBindingContainer bindingContainer =
            (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        //BindingContext bindingContext = BindingContext.getCurrent();
        DCDataControl dc =
            bindingContainer.findDataControl("AppModuleDataControl");// Name of application module in datacontrolBinding.cpx
        AppModuleImpl appM = (AppModuleImpl)dc.getDataProvider();
        return appM;
    }
    AppModuleImpl appM = (AppModuleImpl)this.getAppM();
    

    public void editPopUpFetchListenerFillWarranty(PopupFetchEvent popupFetchEvent) {
        try {
            ViewObject vo = appM.getMnjWarrantyDataLoaderVO1();
            
            vo.setWhereClause("PO_LINE_ID = '" + getPoLineId(appM.getMnjWarrantyManagementHeaderVO1()) + "'");
            
            vo.executeQuery();
            
            warrantyMonthsInputText2.setValue(warrantyPeriodInMonthsBind.getValue());
            AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyMonthsInputText2);
            
            warrantyStartDate.setValue(grnDate.getValue());
            AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyStartDate);
            
            
            setWarrantyExpiryDate(); //calling method overloading
            
            parentItemInputFieldBinding.setValue(null);
            AdfFacesContext.getCurrentInstance().addPartialTarget(parentItemInputFieldBinding);
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
        
    }
    /**
     * Method overloading
     * @throws ParseException
     */
    public void setWarrantyExpiryDate() throws ParseException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(warrantyStartDate.getValue().toString()));
//            if (warrantyMonthsInputTextBind.getValue().toString().equals("0")){
                cal.add(Calendar.MONTH, Integer.parseInt(warrantyMonthsInputText2.getValue().toString()));
//            } else{
//                cal.add(Calendar.MONTH, Integer.parseInt(warrantyMonthsInputTextBind.getValue().toString()));
//            }
            System.out.println("Warranty Expiry Date before casting: " + cal.getTime());
            warrantyExpiryDate.setValue(castToJBODate(cal.getTime().toString(), "E MMM dd HH:mm:ss Z yyyy"));
            System.out.println("Warranty Expiry Date after casting: " + warrantyExpiryDate.getValue());
            AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyExpiryDate); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method overloading
     * @throws ParseException
     */
    public void setWarrantyExpiryDate(String warrantyPeriodMonths) throws ParseException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(warrantyStartDate.getValue().toString()));
            cal.add(Calendar.MONTH, Integer.parseInt(warrantyPeriodMonths));
            System.out.println("Warranty Expiry Date: " + cal.getTime());
            warrantyExpiryDate.setValue(cal.getTime());
            AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyExpiryDate); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method overloading
     * @throws ParseException
     */
    
    public void setWarrantyExpiryDate(int warrantyPeriodMonths) throws ParseException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(warrantyStartDate.getValue().toString()));
            cal.add(Calendar.MONTH, warrantyPeriodMonths);
            System.out.println("Warranty Expiry Date: " + cal.getTime());
            warrantyExpiryDate.setValue(castToJBODate(cal.getTime().toString(), "E MMM dd HH:mm:ss Z yyyy"));
            AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyExpiryDate); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method overloading
     * @throws ParseException
     */
    public void setWarrantyExpiryDate(ViewObject vo, String changedWarrStartDate) throws ParseException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(changedWarrStartDate));
            cal.add(Calendar.MONTH, Integer.parseInt(warrPeriodMonthsDataLoader.getValue().toString()));
            System.out.println("Warranty Expiry Date: " + cal.getTime());
            vo.getCurrentRow().setAttribute("WarrantyEndDate", cal.getTime());
            AdfFacesContext.getCurrentInstance().addPartialTarget(warrExpiryDateDataLoader); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Method overloading
     * @throws ParseException
     */
    public void setWarrantyExpiryDate(ViewObject vo, ValueChangeEvent valueChangeEvent) throws ParseException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(warrStrtDateDataLoader.getValue().toString()));
            cal.add(Calendar.MONTH, Integer.parseInt(valueChangeEvent.getNewValue().toString()));
            System.out.println("Warranty Expiry Date: " + cal.getTime());
            vo.getCurrentRow().setAttribute("WarrantyEndDate", cal.getTime());
            AdfFacesContext.getCurrentInstance().addPartialTarget(warrExpiryDateDataLoader); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public String getPoLineId(ViewObject vo){
        try {
            String poLineId = poLineIdBindingOuttext.getValue().toString();
            System.out.println("current po line id: " + poLineId);
            
            return poLineId; 
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
      return null;    
    }

    public void setProductIDInputTextBind(RichInputText productIDInputTextBind) {
        this.productIDInputTextBind = productIDInputTextBind;
    }

    public RichInputText getProductIDInputTextBind() {
        return productIDInputTextBind;
    }

    public void setWarrantyMonthsInputTextBind(RichInputText warrantyMonthsInputTextBind) {
        this.warrantyMonthsInputTextBind = warrantyMonthsInputTextBind;
    }

    public RichInputText getWarrantyMonthsInputTextBind() {
        return warrantyMonthsInputTextBind;
    }

    public void setWarrantyExpiryDateInputDateBind(RichInputDate warrantyExpiryDateInputDateBind) {
        this.warrantyExpiryDateInputDateBind = warrantyExpiryDateInputDateBind;
    }

    public RichInputDate getWarrantyExpiryDateInputDateBind() {
        return warrantyExpiryDateInputDateBind;
    }

    public void setWarrantyRemainingDaysInputTextBind(RichInputText warrantyRemainingDaysInputTextBind) {
        this.warrantyRemainingDaysInputTextBind = warrantyRemainingDaysInputTextBind;
    }

    public RichInputText getWarrantyRemainingDaysInputTextBind() {
        return warrantyRemainingDaysInputTextBind;
    }

    public void setFixedAssetTagInputTextBind(RichInputText fixedAssetTagInputTextBind) {
        this.fixedAssetTagInputTextBind = fixedAssetTagInputTextBind;
    }

    public RichInputText getFixedAssetTagInputTextBind() {
        return fixedAssetTagInputTextBind;
    }

    public void setProductLocationInputTextBind(RichInputText productLocationInputTextBind) {
        this.productLocationInputTextBind = productLocationInputTextBind;
    }

    public RichInputText getProductLocationInputTextBind() {
        return productLocationInputTextBind;
    }

    public void setProductUserInputTextBind(RichInputText productUserInputTextBind) {
        this.productUserInputTextBind = productUserInputTextBind;
    }

    public RichInputText getProductUserInputTextBind() {
        return productUserInputTextBind;
    }

    public void fillWarrantyInfoDialogListener(DialogEvent dialogEvent) {
        System.out.println("Enter in FillInfoDialogListener....");
        ViewObject vo1 = appM.getWarrantyGRNVO1();
        try {
            if (dialogEvent.getOutcome().name().equals("yes")){
                System.out.println("Enter in yes 1...");

                    if (!getGrnQtyBinding().getValue().toString().equals("0")){
                        ViewObject vo = appM.getMnjWarrantyDataLoaderVO1();
                        row = vo.getAllRowsInRange();
                        voLength = row.length;
                        System.out.println("enter in <======> grn qty  == 1 or grn qty > 1");
//                        System.out.println("voLength: " + voLength);
                            if (voLength == 0){
                                System.out.println("enter in volength == 0, " + "voLength: " + voLength);
//                              warnTable(dataLoaderTable, "No Data Found In Warranty Info Loader Table!");
                                ResetUtils.reset(fillInfoPanelGroupLayoutBinding);

//                                warrantyTypeLOVBinding.setValue("");
            
                                AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyTypeLOVBinding);
 
                                showMessage("Found No Generated Warranty Info!", "warn");
                                
                            }
                            else if (voLength > 0){
                                System.out.println("enter in volength > 0, " +"voLength: " + voLength);
                                System.out.println("WarrantyType: " + getWarrantyTypeLOVBinding().getValue());

                                System.out.println("Warranty Months: " + getWarrantyMonthsInputTextBind().getValue());

                                System.out.println("Major Category: " + getMajorCategoryBinding().getValue());
                                System.out.println("Minor Categroy: " + getSubCategoryBinding().getValue());
                                for (Row r : row ){
                                    System.out.println("enter for-each loop 1...");
                                    System.out.println("row product s/n: " + r.getAttribute("ProductSerialNo"));
                                    if (r.getAttribute("ProductSerialNo") == null){
                                        warnInputText(prodSrNumDataLoadBinding, "Please Enter Product S/N!");
                                        
                                    }
                                    
//                                    System.out.println("getAllProductSnValue() returns: " + getAllProductSnValue());
                                    else if(getAllProductSnValue() == "allOK") {
                                        System.out.println("enter pop up condition!");
                                        RichPopup.PopupHints hints = new RichPopup.PopupHints();
                                        getSavePopUpBinding().show(hints);// pop up will appear
                                    }
                                }
                                
                            }
                    }

            }
            else if (dialogEvent.getOutcome().name().equals("no")){
                    System.out.println("Enter in no 1...");
//                    ResetUtils.reset(fillInfoPanelGroupLayoutBinding);
//    
//                    locationLOVBinding.setValue("");
//                    userLOVBinding.setValue("");
//                    warrantyTypeLOVBinding.setValue("");
//
//                    subLocationBinding.setValue("");
                    
                    AdfFacesContext.getCurrentInstance().addPartialTarget(locationLOVBinding);
                    AdfFacesContext.getCurrentInstance().addPartialTarget(userLOVBinding);
                    AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyTypeLOVBinding);

                    AdfFacesContext.getCurrentInstance().addPartialTarget(subLocationBinding);
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
    }
    
    public String getAllProductSnValue(){
        try {
            ViewObject vo = appM.getMnjWarrantyDataLoaderVO1();
            RowSetIterator it = vo.createRowSetIterator("");
            Row[] dataLoaderRowArray = vo.getAllRowsInRange();
            String returnProdsl = "";
            
            allOK = "allOK";
            
            for (Row r : dataLoaderRowArray){

                System.out.println("enter for-each loop 2...");
                System.out.println("prod s/n inside getAllProductSnValue: " + r.getAttribute("ProductSerialNo"));
                if (r.getAttribute("ProductSerialNo") == null){
                    System.out.println("******* return null");
                    return null;
                }
            }   
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allOK;   
    }

    public void setFillInfoPanelGroupLayoutBinding(RichPanelGroupLayout fillInfoPanelGroupLayoutBinding) {
        this.fillInfoPanelGroupLayoutBinding = fillInfoPanelGroupLayoutBinding;
    }

    public RichPanelGroupLayout getFillInfoPanelGroupLayoutBinding() {
        return fillInfoPanelGroupLayoutBinding;
    }

    public void setLocationLOVBinding(RichInputListOfValues locationLOVBinding) {
        this.locationLOVBinding = locationLOVBinding;
    }

    public RichInputListOfValues getLocationLOVBinding() {
        return locationLOVBinding;
    }

    public void setUserLOVBinding(RichInputListOfValues userLOVBinding) {
        this.userLOVBinding = userLOVBinding;
    }

    public RichInputListOfValues getUserLOVBinding() {
        return userLOVBinding;
    }
    
    public void insertInWarrantyHeader(ViewObject vo, int grnQty){
        System.out.println("Enter Insert In Warranty Header....");
        
        try {
                if (parentItemInputFieldBinding.getValue() == null || parentItemInputFieldBinding.getValue().equals("")){ //insert in vo header
                    System.out.println("   >>> enter in if condition ---> while 'NO' parent item");
                    Row createdRow = vo.createRow();
                    createdRow.setAttribute("OrgId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("Ou"));
                    createdRow.setAttribute("Organization", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("OrganizationCode"));
                    createdRow.setAttribute("SupplierName", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("Supplier"));
                    createdRow.setAttribute("Spo", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoNumber"));
                    createdRow.setAttribute("ItemId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("ItemId"));
                    createdRow.setAttribute("ItemDescription", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("ItemDescription"));
                    createdRow.setAttribute("Grn", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnNumber"));
                    createdRow.setAttribute("GrnDate", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnDate"));
                    createdRow.setAttribute("Qty", 1);
                    createdRow.setAttribute("Uom", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnOum"));
                    createdRow.setAttribute("PoLineId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoLineId"));
                    createdRow.setAttribute("VendorId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("VendorId"));
                    createdRow.setAttribute("PoHeaderId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoHeaderId"));
                    
//                    createdRow.setAttribute("ItemCategory", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemCategory"));
//                    createdRow.setAttribute("ItemCategoryId", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemCategoryId"));
                    createdRow.setAttribute("ItemCategoryShort", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemCategoryShort"));
//                    createdRow.setAttribute("ItemSubCategory", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemSubCategory"));
//                    createdRow.setAttribute("ItemSubCategoryId", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemSubCategoryId"));
                    createdRow.setAttribute("ItemSubCategoryShort", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemSubCategoryShort"));
                    createdRow.setAttribute("WarrantyTagYear", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyTagYear"));
                    createdRow.setAttribute("WarrantyType", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyType"));
                    createdRow.setAttribute("WarrantyMonths", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyMonths"));
                    System.out.println("Product S/N: " + appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ProductSerialNo"));
                    createdRow.setAttribute("ProductSerialNo", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ProductSerialNo"));
                    createdRow.setAttribute("FixedAssetTagId", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("FixedAssetTagId"));
                    createdRow.setAttribute("Location", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("Location"));
                    createdRow.setAttribute("CostCenterName", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("CostCenterName"));
                    createdRow.setAttribute("CostCenter", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("CostCenter"));
                    createdRow.setAttribute("ProductUser", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ProductUser"));
                    createdRow.setAttribute("ProductUserEmpId", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ProductUserEmpId"));
                    createdRow.setAttribute("ProductUserDept", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ProductUserDept"));
                    createdRow.setAttribute("SubLocation", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("SubLocation"));
                    createdRow.setAttribute("Remarks", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("Remarks"));
                    createdRow.setAttribute("ScheduleMaintainFlag", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ScheduleMaintainFlag"));
                    createdRow.setAttribute("MaintainPeriod", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("MaintainPeriod"));
                    createdRow.setAttribute("NextMaintainDate", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("NextMaintainDate"));
                    createdRow.setAttribute("WarrantyStartDate", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyStartDate"));
                    createdRow.setAttribute("WarrantyEndDate", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyEndDate"));
                    
                    String unit = getOperatingUnitBinding().getValue().toString();
                    String grnNumber = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnNumber").toString();
                    String itemId = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("ItemId").toString();
                    orgName = getOrganizationNameBinding().getValue().toString();
                    majorCat = appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemCategoryShort").toString();
                    minorCat = appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemSubCategoryShort").toString();
                    tagYear =  appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyTagYear").toString();
                    createdRow.setAttribute("WarrantyItemSerialNumber", Integer.parseInt(getSrNoString(orgName, majorCat, minorCat, tagYear)));
                    createdRow.setAttribute("WarrantyTag", generateWarrantyTag(orgName, majorCat, minorCat, tagYear, getSrNoString(orgName, majorCat, minorCat, tagYear)));
                    vo.insertRow(createdRow);  
                }
                else{
                    System.out.println("  >>>>> enter in else statement ---> when parent item 'EXISTS'");
                    System.out.println(">>>>>>>>>>>>>>>>> Parent Tag: " + parentItemInputFieldBinding.getValue());
                    System.out.println(">>>>>>>>>>>>> HeaderId: " + appM.getMnjWarrDataLoaderSetParVO1().getCurrentRow().getAttribute("HeaderId"));
                    insertInLineVo(appM.getMnjWarrantyManagementLineVO1(), appM.getMnjWarrDataLoaderSetParVO1(), appM.getMnjWarrantyDataLoaderVO1(), appM.getMnjWarrantyManagementHeaderVO1(), appM.getWarrantyGRNVO1());
                }
            } catch (Exception e) {
                showMessage(e.toString(), "warn");
                e.printStackTrace();
            }   
    }
    
    public void showMessage(String message, String severity){
        
        FacesMessage fm = new FacesMessage(message);
        
        if(severity.equals("info")){
            fm.setSeverity(FacesMessage.SEVERITY_INFO);    
        }
        else if (severity.equals("warn")){
            fm.setSeverity(FacesMessage.SEVERITY_WARN);       
        }
        else if (severity.equals("error")){
            fm.setSeverity(FacesMessage.SEVERITY_ERROR);    
        }
        
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, fm);
    }

    public void setMnjWarrantyHeaderTable(RichTable mnjWarrantyHeaderTable) {
        this.mnjWarrantyHeaderTable = mnjWarrantyHeaderTable;
    }

    public RichTable getMnjWarrantyHeaderTable() {
        return mnjWarrantyHeaderTable;
    }

    public void setWarrantyGRNTable(RichTable warrantyGRNTable) {
        this.warrantyGRNTable = warrantyGRNTable;
    }

    public RichTable getWarrantyGRNTable() {
        return warrantyGRNTable;
    }

    public void setWarrantyTypeLOVBinding(RichSelectOneChoice warrantyTypeLOVBinding) {
        this.warrantyTypeLOVBinding = warrantyTypeLOVBinding;
    }

    public RichSelectOneChoice getWarrantyTypeLOVBinding() {
        return warrantyTypeLOVBinding;
    }

    public void customSave(ActionEvent actionEvent) {
        System.out.println("Enter in custom Save..........");
        try {
            save(); // this commit is for finding out the parent-child row
            ViewObject vo = appM.getMnjWarrantyManagementHeaderVO1();
            vo.setRangeSize(Integer.parseInt(getWarrantyHeaderVOEstimatedRowBinding().getValue().toString()));
            Row[] voRowArray = vo.getAllRowsInRange();
            int count = 0;
            System.out.println("Loop initiates....");
            for(Row row : voRowArray){
                if (row.getAttribute("LineTableRowNum").equals("0")){
                    count++;
                    System.out.println("  With no child rows: " + count + ", ChidExists: " + row.getAttribute("ChildExistsFlag"));
                    if (row.getAttribute("ChildExistsFlag").equals("Yes")){
                        System.out.println("   Enter in Yes....");
                        row.setAttribute("ChildExistsFlag", "No");
                    }
                    
                }
            }
            System.out.println("Loop terminates; Found Total number of rows with no childs: " + count );
            save();
            showMessage("Record Saved Successfully!", "info");
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
    }
    
    public void save(){
        
        System.out.println("       Enter in save....");
        
        try {
            appM.getDBTransaction().commit();
            refreshAllVO();            
        } catch (Exception e) {
             if (e.toString().contains("Failed to validate all rows in a transaction")){
                showMessage("Warranty Tag Not Found!", "warn");
                
            
                
             }else if (e.toString().contains("MNJ_WARRANTY_MANAGEMENT_H_U02")){
                showMessage("Found Duplicate Fixed Asset Tag/N!", "warn");
             }
             
            else if (e.toString().contains("MNJ_WARRANTY_MANAGEMENT_LI_U01")){
                            showMessage("Found Duplicate Warranty Tag In Sub-Items Table!", "warn");
                         }
             
             else {
                    showMessage(e.toString(), "warn");
                    e.printStackTrace();
            }
        }
    }

    public void saveDialogListener(DialogEvent dialogEvent) { 
        System.out.println("Enter in saveDialogListener...");
//        System.out.println("dialogEvent.getOutcome().name() = " + dialogEvent.getOutcome().name());
     try {
            if (dialogEvent.getOutcome().name().equals("yes")){
                System.out.println("  Enter in yes 2...");
                ViewObject vo = appM.getMnjWarrantyManagementHeaderVO1();                
                if (getGrnQtyBinding().getValue().toString().equals("1")){
                    System.out.println("++++++++++++Grn Qty == 1");
                    int grnQty = Integer.parseInt(getGrnQtyBinding().getValue().toString()) ;
                    insertInWarrantyHeader(vo, grnQty); // method for inserting data in MASTER_TABLE
                    appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().remove();
                    save();
                    refreshAllVO();

                }else {
                    System.out.println("++++++++++++Grn Qty > 1");
                    int grnQty = Integer.parseInt(getGrnQtyBinding().getValue().toString()) ;
                    System.out.println("======Grn Qty:" + grnQty);
                    
                    int iterator = 0;
                    int i = 0 ;
                    while (iterator < grnQty){ 
                        i = iterator + 1;
                        System.out.println("++++Row number: " + i);
                        insertInWarrantyHeader(vo, grnQty);// method for inserting data in MASTER_TABLE
                        appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().remove(); 
                        appM.getDBTransaction().commit();
                        iterator++;
                    }
                   refreshAllVO();  
                }  
            }else if (dialogEvent.getOutcome().name().equals("no")){
                System.out.println("  Enter in no 2....");
//                ResetUtils.reset(fillInfoPanelGroupLayoutBinding);
//                locationLOVBinding.setValue("");
//                userLOVBinding.setValue("");
//                warrantyTypeLOVBinding.setValue("");
//                majorCategoryBinding.setValue("");
//                subCategoryBinding.setValue("");
//                subLocationBinding.setValue("");
                
                AdfFacesContext.getCurrentInstance().addPartialTarget(locationLOVBinding);
                AdfFacesContext.getCurrentInstance().addPartialTarget(userLOVBinding);
                AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyTypeLOVBinding);
                AdfFacesContext.getCurrentInstance().addPartialTarget(majorCategoryBinding);
                AdfFacesContext.getCurrentInstance().addPartialTarget(subCategoryBinding);
                AdfFacesContext.getCurrentInstance().addPartialTarget(subLocationBinding);

            }
         
        } catch (Exception e) {
         
                if (e.toString().contains("MNJ_WARRANTY_MANAGEMENT_H_U02")){
                    showMessage("Found Duplicate Fixed Asset Tag/N!", "warn");
                } else {
                    showMessage(e.toString(), "warn");
                    e.printStackTrace();
                }
        }
    }

    public void setSavePopUpBinding(RichPopup savePopUpBinding) {
        this.savePopUpBinding = savePopUpBinding;
    }

    public RichPopup getSavePopUpBinding() {
        return savePopUpBinding;
    }

    public void setGrNVOEstimatedRowBinding(RichOutputText grNVOEstimatedRowBinding) {
        this.grNVOEstimatedRowBinding = grNVOEstimatedRowBinding;
    }

    public RichOutputText getGrNVOEstimatedRowBinding() {
        return grNVOEstimatedRowBinding;
    }

    public void setWarrantyHeaderVOEstimatedRowBinding(RichOutputText warrantyHeaderVOEstimatedRowBinding) {
        this.warrantyHeaderVOEstimatedRowBinding = warrantyHeaderVOEstimatedRowBinding;
    }

    public RichOutputText getWarrantyHeaderVOEstimatedRowBinding() {
        return warrantyHeaderVOEstimatedRowBinding;
    }

//    public void generateWarrantyTagButtonAction(ActionEvent actionEvent) {
//        System.out.println("Enter in generateWarrantyTag...");
//        
//        
//       
//        try {
//            ViewObject vo = appM.getMnjWarrantyManagementHeaderVO1();
//            ViewObject vo1 = appM.getWarrantyGRNVO1();
//            String unit = getOperatingUnitBinding().getValue().toString();
//            String itemid = getItemIdBinding().getValue().toString();
//            orgName = getOrganizationNameBinding().getValue().toString();
//            String grnNumber = getGrnNumberBinding().getValue().toString();
//            majorCat = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MajorCategoryShort").toString();
//            minorCat = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MinorCategoryShort").toString();
//            System.out.println("Current Unit:" + unit);
//            System.out.println("Current Org Name:" + orgName);
//            System.out.println("Current GrnNumber:" + grnNumber);
//            System.out.println("majorCategory:" + majorCat);
//            System.out.println("minorCategory:" + minorCat);
//            System.out.println("itemSerial:" +getSrNoString(unit, majorCat, minorCat));
//            
//            if (warrantyTypeLOVBinding.getValue() == null || warrantyTypeLOVBinding.getValue() == "" || getWarrantyTypeLOVBinding().getValue().toString().equals("0") ){
//                
//               warnSelectOneChoice(warrantyTypeLOVBinding, "Please Enter Warranty Type!");
//               
//            } else if (warrantyMonthsInputText2.getValue() == null || warrantyMonthsInputText2.getValue() == ""){
//                
//               warnInputText(warrantyMonthsInputText2, "Please Enter Warranty Months!");
//               
//            } else if (productIDInputTextBind.getValue() == null || productIDInputTextBind.getValue() == "" ){
//            
//               warnInputText(productIDInputTextBind, "Please Enter Product S/N!");
//               
//            } else {
//            generateWarrantyTag(unit, grnNumber, itemid, majorCat, minorCat, getSrNoString(unit, majorCat, minorCat)); //method for generating Warranty Tag
//            }
//        } catch (Exception e) {
//            showMessage(e.toString(), "warn");
//            e.printStackTrace();
//        }
//        
//        
//    }
    
    public String generateWarrantyTag(String Org, String majorCategory, String minorCategory, String warrantyYear, String itemSerial){
        try {
            if (grnQtyBinding.getValue().toString().equals("1")){
                    System.out.println("Tag generate --> GRN Qty == 1");
                    String warrTagNumber = Org.concat("/").concat(majorCategory).concat("/").concat(minorCategory).concat("/").concat(warrantyYear).concat("/").concat(itemSerial);
                    System.out.println("warrTagNumber:" + warrTagNumber);
//                    String warrTagNumber = warrantyItemSerialGenerate(concatedWarTag);
                    
                    return warrTagNumber;
//                    warrantyTagNumberBinding.setValue(warrTagNumber);
                }
            else {
//                showMessage("More Than 1 GRN Qty!", "info");
                System.out.println("Tag generate --> GRN Qty > 1");  
                String warrTagNumber = Org.concat("/").concat(majorCategory).concat("/").concat(minorCategory).concat("/").concat(warrantyYear).concat("/").concat(itemSerial);
                System.out.println("warrTagNumber:" + warrTagNumber);
                return warrTagNumber;
            }
            
        } catch (Exception e) {
         
            e.printStackTrace();
        }
       return null; 
    }

    public void setMajorCategoryBinding(RichInputListOfValues majorCategoryBinding) {
        this.majorCategoryBinding = majorCategoryBinding;
    }

    public RichInputListOfValues getMajorCategoryBinding() {
        return majorCategoryBinding;
    }

    public void setSubCategoryBinding(RichInputListOfValues subCategoryBinding) {
        this.subCategoryBinding = subCategoryBinding;
    }

    public RichInputListOfValues getSubCategoryBinding() {
        return subCategoryBinding;
    }

    public void setWarrantyTagNumberBinding(RichInputText warrantyTagNumberBinding) {
        this.warrantyTagNumberBinding = warrantyTagNumberBinding;
    }

    public RichInputText getWarrantyTagNumberBinding() {
        return warrantyTagNumberBinding;
    }

    public void setOperatingUnitBinding(RichOutputText operatingUnitBinding) {
        this.operatingUnitBinding = operatingUnitBinding;
    }

    public RichOutputText getOperatingUnitBinding() {
        return operatingUnitBinding;
    }

    public void setGrnNumberBinding(RichOutputText grnNumberBinding) {
        this.grnNumberBinding = grnNumberBinding;
    }

    public RichOutputText getGrnNumberBinding() {
        return grnNumberBinding;
    }

    public void setGrnQtyBinding(RichOutputText grnQtyBinding) {
        this.grnQtyBinding = grnQtyBinding;
    }

    public RichOutputText getGrnQtyBinding() {
        return grnQtyBinding;
    }
    
//    public String warrantyItemSerialGenerate(String concatedString){
//        try {
//            String itemSerial = getSrNoString();
//            
//            String warrantyTag = concatedString.concat("/").concat(itemSerial);
//                    
////                 System.out.println("itemSerial: " + itemSerial);
//                    
//                    System.out.println("warrantyTag:" + warrantyTag);
//            return warrantyTag;  
//        } catch (Exception e) {
//            
//            e.printStackTrace();
//        }
//        
//     return null;
//    }
    /**
     * method for generating Unique Warranty Item Serial for header table
     */
    public String getSrNoString(String s1, String s2, String s3, String s4){ 
       System.out.println("  Enter in getSrNoString()....");
        String srno = "";

        try {
            String stmnt = "BEGIN :1 := CUST_MNJ_ONT_PKG.warranty_management_item_srno(:2, :3, :4, :5); end;";
            
            CallableStatement cs = appM.getDBTransaction().createCallableStatement(stmnt, 1);
            cs.setString(2, s1);
            cs.setString(3, s2);
            cs.setString(4, s3);
            cs.setString(5, s4);
//            cs.setString(1, "GC");
//            cs.setString(2, "LAPTOP");
            cs.registerOutParameter(1, OracleTypes.VARCHAR);
            cs.executeQuery();
            srno = cs.getString(1);
            System.out.println("srno:" + srno);
            cs.close();
            
            
        } catch (SQLException e) {
            
            e.printStackTrace();
        }
        return srno;
    }
    
    /**
     * method for generating Unique Warranty Item Serial for line table
     */
    public String getSrNoStringLine(String s1, String s2, String s3, String s4){ 
       System.out.println("  Enter in getSrNoStringLine()....");
        String srno = "";

        try {
  

            String stmnt = "BEGIN :1 := CUST_MNJ_ONT_PKG.warranty_management_srno_line(:2, :3, :4, :5); end;";
            
            CallableStatement cs = appM.getDBTransaction().createCallableStatement(stmnt, 1);
            cs.setString(2, s1);
            cs.setString(3, s2);
            cs.setString(4, s3);
            cs.setString(5, s4);
    //            cs.setString(1, "GC");
    //            cs.setString(2, "LAPTOP");
            cs.registerOutParameter(1, OracleTypes.VARCHAR);
            cs.executeQuery();
            srno = cs.getString(1);
            System.out.println("srno line level:" + srno);
            cs.close();
            
            
        } catch (SQLException e) {
            
            e.printStackTrace();
        }
        return srno;
    }
    



    public void setOrganizationNameBinding(RichOutputText organizationNameBinding) {
        this.organizationNameBinding = organizationNameBinding;
    }

    public RichOutputText getOrganizationNameBinding() {
        return organizationNameBinding;
    }

    public void setProductSerialNumberBinding(RichInputText productSerialNumberBinding) {
        this.productSerialNumberBinding = productSerialNumberBinding;
    }

    public RichInputText getProductSerialNumberBinding() {
        return productSerialNumberBinding;
    }
    
    public void warnInputText(RichInputText inputText, String message){
        FacesMessage Message = new FacesMessage(message);
        Message.setSeverity(FacesMessage.SEVERITY_WARN);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(inputText.getClientId(fc), Message);
        
        
    }
    
    public void warnInputDate(RichInputDate inputDate, String message){
        FacesMessage Message = new FacesMessage(message);
        Message.setSeverity(FacesMessage.SEVERITY_WARN);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(inputDate.getClientId(fc), Message);
    }
    
    public void warnInputListOfValues(RichInputListOfValues inputListOfValues, String message){
        FacesMessage Message = new FacesMessage(message);
        Message.setSeverity(FacesMessage.SEVERITY_WARN);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(inputListOfValues.getClientId(fc), Message);
        
        
    }
    
    public void warnSelectOneChoice(RichSelectOneChoice selectOneChoice, String message){
        FacesMessage Message = new FacesMessage(message);
        Message.setSeverity(FacesMessage.SEVERITY_WARN);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(selectOneChoice.getClientId(fc), Message);
        
        
    }
    
    public void warnTable(RichTable table, String message){
        FacesMessage Message = new FacesMessage(message);
        Message.setSeverity(FacesMessage.SEVERITY_WARN);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(table.getClientId(fc), Message);
        
        
    }
    
   

    public void setSubLocationBinding(RichInputListOfValues subLocationBinding) {
        this.subLocationBinding = subLocationBinding;
    }

    public RichInputListOfValues getSubLocationBinding() {
        return subLocationBinding;
    }
    
    public void refreshAllVO(){
        System.out.println("                     Enter in refreshAllVO....");
        ViewObject vo1, vo2, vo3, vo4;
        
        vo1 = appM.getWarrantyGRNVO1();
        vo2 = appM.getMnjWarrantyManagementHeaderVO1();
        vo3 = appM.getMnjWarrantyDataLoaderVO1();
        vo4 = appM.getMnjWarrantyManagementLineVO1();
        
        vo1.executeQuery();
        vo2.executeQuery();
        vo3.executeQuery();
        vo4.executeQuery();
        
        
//        ResetUtils.reset(fillInfoPanelGroupLayoutBinding);

//        locationLOVBinding.setValue("");
//        userLOVBinding.setValue("");
//        warrantyTypeLOVBinding.setValue("");
//        majorCategoryBinding.setValue("");
//        subCategoryBinding.setValue("");
//        subLocationBinding.setValue("");
            
        AdfFacesContext.getCurrentInstance().addPartialTarget(locationLOVBinding);
        AdfFacesContext.getCurrentInstance().addPartialTarget(userLOVBinding);
        AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyTypeLOVBinding);
        AdfFacesContext.getCurrentInstance().addPartialTarget(majorCategoryBinding);
        AdfFacesContext.getCurrentInstance().addPartialTarget(subCategoryBinding);
        AdfFacesContext.getCurrentInstance().addPartialTarget(subLocationBinding);
        AdfFacesContext.getCurrentInstance().addPartialTarget(mnjWarrantyHeaderTable);
        AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyGRNTable);
        AdfFacesContext.getCurrentInstance().addPartialTarget(grNVOEstimatedRowBinding);
        AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyHeaderVOEstimatedRowBinding);
        AdfFacesContext.getCurrentInstance().addPartialTarget(dataLoaderTable);
        AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyLineTable);

//        ResetUtils.reset(warrantyGRNTable);
//        ResetUtils.reset(mnjWarrantyHeaderTable);
//        ResetUtils.reset(grNVOEstimatedRowBinding);
//        ResetUtils.reset(warrantyHeaderVOEstimatedRowBinding);
        
        
        
        
    }

    public void itemSubCatValChangeListener(ValueChangeEvent valueChangeEvent) {
        System.out.println("itemSubCatValChangeListener...");
        
        
        
        
    }

    public void generateMultiWarrantyTagButtonAction(ActionEvent actionEvent) {
        System.out.println("Enter in generateMultiWarrantyTagButtonAction....");
        String maintain_Flag;
        try {
            maintain_Flag = getMaintainFlag().getValue().toString();
            System.out.println("Maintain Flag Value: " + maintain_Flag);
        } catch (Exception e) {
            maintain_Flag = "false";
        }
        System.out.println("war type: " + warrantyTypeLOVBinding.getValue());
        System.out.println("war months from GRN: " + warrantyPeriodInMonthsBind.getValue());
        System.out.println("war months while spo given warranty period == 0: " + warrantyMonthsInputText2.getValue());
        System.out.println("war months while spo given warranty period != 0: " + warrantyMonthsInputTextBind.getValue());
        System.out.println("major cat: " + majorCategoryBinding.getValue());
        System.out.println("minor cat: " + subCategoryBinding.getValue());
        
         if (warrantyTypeLOVBinding.getValue() == null || warrantyTypeLOVBinding.getValue() == "" || getWarrantyTypeLOVBinding().getValue().toString().equals("0") ){
             
            warnSelectOneChoice(warrantyTypeLOVBinding, "Please Enter Warranty Type!");
            
        } else if (warrantyStartDate.getValue() == null || warrantyStartDate.getValue() == ""){
                 warnInputDate(warrantyStartDate, "Please Enter Warranty Start Date!");
                 
        } else if (maintain_Flag.equals("true") && (maintainPeriodCapture.getValue() == null || maintainPeriodCapture.getValue() == "")){
                System.out.println("into period null....");
                 warnInputText(maintainPeriodCapture, "Please Enter Maintain Period!");
                 
        } else if (warrantyMonthsInputText2.getValue() == null || warrantyMonthsInputText2.getValue() == ""){
             
            warnInputText(warrantyMonthsInputText2, "Please Enter Warranty Months!");
            
        }else if (warrantyMonthsInputTextBind.getValue() == null || warrantyMonthsInputTextBind.getValue() == ""){
             
            warnInputText(warrantyMonthsInputTextBind, "Please Enter Warranty Months!");
            
        } 
//         else if (majorCategoryBinding.getValue() == null || majorCategoryBinding.getValue() == ""){
//             
//            warnInputListOfValues(majorCategoryBinding, "Please Enter Item Category!");
//
//        } else if (subCategoryBinding.getValue() == null || subCategoryBinding.getValue() == ""){
//             
//            warnInputListOfValues(subCategoryBinding, "Please Enter Item Sub-Category!");
//         } 
        
        else {
             try {
                System.out.println("enter into else....");
                int grnQty = Integer.parseInt(grnQtyBinding.getValue().toString()) ;
                System.out.println("grn qty: " + grnQty);
                int iterator = 0; 
                int i = 0;
                 while (iterator < grnQty){
                     i = iterator + 1;
                     System.out.println("++++Row number: " + i);
                     
                     insertInDataLoader(appM.getMnjWarrantyDataLoaderVO1());
                     appM.getDBTransaction().commit();
                     iterator++;    
                 } 
                 
                 AdfFacesContext.getCurrentInstance().addPartialTarget(dataLoaderTable);
            } catch (Exception e) {
                e.printStackTrace();
            }
  
        }
        
    }
    
    public void insertInDataLoader(ViewObject vo){
        try {
            System.out.println("Enter in insertInDataLoader....");
            Row createdRow = vo.createRow();
            String grnNumber, maintain_Flag = null;
            
            warrantyType = getWarrantyTypeLOVBinding().getValue().toString();
            warrantyStartDateC = getWarrantyStartDate().getValue().toString();
            warrantyExpiryDateC = getWarrantyExpiryDate().getValue().toString();
            System.out.println("warranty start date C: " + warrantyStartDateC +"\n"+ 
                               "warranty end date C: " + warrantyExpiryDateC);
            
            try {
                 grnNumber = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnNumber").toString();
            } catch (Exception e) {
                ;
            }
            try {
                maintain_Flag = getMaintainFlag().getValue().toString();
                System.out.println("Maintain Flag Value: " + maintain_Flag);
            } catch (Exception e) {
                maintain_Flag = "false";
            }
            // if else added by Mr. Sakibul Islam on 12.Mar.2020
            if(maintain_Flag == "true"){
                System.out.println("into Maintain Flag ---> Y.............");
                createdRow.setAttribute("OrgId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("Ou"));
                createdRow.setAttribute("Organization", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("OrganizationCode"));
                createdRow.setAttribute("SupplierName", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("Supplier"));
                createdRow.setAttribute("Spo", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoNumber"));
                createdRow.setAttribute("ItemId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("ItemId"));
                createdRow.setAttribute("ItemDescription", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("ItemDescription"));
                createdRow.setAttribute("Grn", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnNumber"));
                createdRow.setAttribute("GrnDate", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnDate"));
                createdRow.setAttribute("Qty", 1);
                createdRow.setAttribute("Uom", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnOum"));
                createdRow.setAttribute("PoLineId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoLineId"));
                createdRow.setAttribute("VendorId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("VendorId"));
                createdRow.setAttribute("PoHeaderId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoHeaderId"));
//                createdRow.setAttribute("ItemCategory", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MajorCategoryLOV"));
//                createdRow.setAttribute("ItemCategoryId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MajorCategoyId"));
                createdRow.setAttribute("ItemCategoryShort", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MajorDescription"));
//                createdRow.setAttribute("ItemSubCategory", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MinorCategoryDesc"));
//                createdRow.setAttribute("ItemSubCategoryId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MinorCategoryId"));
                createdRow.setAttribute("ItemSubCategoryShort", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MinorDescription"));
                createdRow.setAttribute("WarrantyTagYear", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("CurrentYear"));
                createdRow.setAttribute("ScheduleMaintainFlag", "Y"); // added by Mr. Sakibul Islam on 12.Mar.2020
                createdRow.setAttribute("MaintainPeriod", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("SchedulePeriod")); // added by Mr. Sakibul Islam on 18.Mar.2020
                createdRow.setAttribute("NextMaintainDate", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("NextMaintainDate")); // added by Mr. Sakibul Islam on 18.Mar.2020
                
                createdRow.setAttribute("WarrantyStartDate", castToJBODate(warrantyStartDateC, "dd-MMM-yyyy")); // added by Mr. Sakibul Islam on 08.Apr.2020
            //            createdRow.setAttribute("WarrantyEndDate", castToJBODate(warrantyExpiryDateC, "E MMM dd HH:mm:ss Z yyyy")); // added by Mr. Sakibul Islam on 08.Apr.2020
                createdRow.setAttribute("WarrantyEndDate", castToJBODate(getCalculatedExpiryDate(), "E MMM dd HH:mm:ss Z yyyy")); // added by Mr. Sakibul Islam on 08.Apr.2020
//                orgName = getOrganizationNameBinding().getValue().toString();
//                String unit = getOperatingUnitBinding().getValue().toString();
//                majorCat = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MajorCategoryShort").toString();
//                minorCat = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MinorCategoryShort").toString();
                createdRow.setAttribute("WarrantyType", warrantyType.equals("0") ? "" : warrantyType.equals("1") ? "Guarantee" : warrantyType.equals("2") ? "Service" : "Warranty");
                System.out.println("warranty months from GRN details: " + getWarrantyPeriodInMonthsBind().getValue());
            //            createdRow.setAttribute("WarrantyMonths", warrantyPeriodInMonthsBind.getValue().equals("0") ? getWarrantyMonthsInputText2().getValue(): getWarrantyMonthsInputTextBind().getValue());
                createdRow.setAttribute("WarrantyMonths", getWarrantyMonthsInputText2().getValue());
//                orgName = getOrganizationNameBinding().getValue().toString();
//                majorCat = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MajorCategoryShort").toString();
//                minorCat = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MinorCategoryShort").toString();
                vo.insertRow(createdRow);
            }
            else{
                createdRow.setAttribute("OrgId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("Ou"));
                createdRow.setAttribute("Organization", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("OrganizationCode"));
                createdRow.setAttribute("SupplierName", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("Supplier"));
                createdRow.setAttribute("Spo", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoNumber"));
                createdRow.setAttribute("ItemId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("ItemId"));
                createdRow.setAttribute("ItemDescription", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("ItemDescription"));
                createdRow.setAttribute("Grn", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnNumber"));
                createdRow.setAttribute("GrnDate", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnDate"));
                createdRow.setAttribute("Qty", 1);
                createdRow.setAttribute("Uom", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnOum"));
                createdRow.setAttribute("PoLineId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoLineId"));
                createdRow.setAttribute("VendorId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("VendorId"));
                createdRow.setAttribute("PoHeaderId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoHeaderId"));
//                createdRow.setAttribute("ItemCategory", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MajorCategoryLOV"));
//                createdRow.setAttribute("ItemCategoryId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MajorCategoyId"));
                createdRow.setAttribute("ItemCategoryShort", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MajorDescription"));
//                createdRow.setAttribute("ItemSubCategory", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MinorCategoryDesc"));
//                createdRow.setAttribute("ItemSubCategoryId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MinorCategoryId"));
                createdRow.setAttribute("ItemSubCategoryShort", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MinorDescription"));
                createdRow.setAttribute("WarrantyTagYear", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("CurrentYear"));
                
                createdRow.setAttribute("ScheduleMaintainFlag", "N"); // added by Mr. Sakibul Islam on 12.Mar.2020
                
                createdRow.setAttribute("WarrantyStartDate", castToJBODate(warrantyStartDateC, "dd-MMM-yyyy")); // added by Mr. Sakibul Islam on 08.Apr.2020
            //            createdRow.setAttribute("WarrantyEndDate", castToJBODate(warrantyExpiryDateC, )); // added by Mr. Sakibul Islam on 08.Apr.2020
                createdRow.setAttribute("WarrantyEndDate", castToJBODate(getCalculatedExpiryDate(), "E MMM dd HH:mm:ss Z yyyy")); // added by Mr. Sakibul Islam on 08.Apr.2020
//                orgName = getOrganizationNameBinding().getValue().toString();
//                String unit = getOperatingUnitBinding().getValue().toString();
//                majorCat = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MajorCategoryShort").toString();
//                minorCat = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MinorCategoryShort").toString();
                createdRow.setAttribute("WarrantyType", warrantyType.equals("0") ? "" : warrantyType.equals("1") ? "Guarantee" : warrantyType.equals("2") ? "Service" : "Warranty");
                System.out.println("warranty months from GRN details: " + getWarrantyPeriodInMonthsBind().getValue());
            //            createdRow.setAttribute("WarrantyMonths", warrantyPeriodInMonthsBind.getValue().equals("0") ? getWarrantyMonthsInputText2().getValue(): getWarrantyMonthsInputTextBind().getValue());
                createdRow.setAttribute("WarrantyMonths", getWarrantyMonthsInputText2().getValue());
//                orgName = getOrganizationNameBinding().getValue().toString();
//                majorCat = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MajorCategoryShort").toString();
//                minorCat = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("MinorCategoryShort").toString();
                vo.insertRow(createdRow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void setDataLoaderTable(RichTable dataLoaderTable) {
        this.dataLoaderTable = dataLoaderTable;
    }

    public RichTable getDataLoaderTable() {
        return dataLoaderTable;
    }

    public void setFixedAssetTagLOV(RichInputListOfValues fixedAssetTagLOV) {
        this.fixedAssetTagLOV = fixedAssetTagLOV;
    }

    public RichInputListOfValues getFixedAssetTagLOV() {
        return fixedAssetTagLOV;
    }

    public void setPoLineIdBindingOuttext(RichOutputText poLineIdBindingOuttext) {
        this.poLineIdBindingOuttext = poLineIdBindingOuttext;
    }

    public RichOutputText getPoLineIdBindingOuttext() {
        return poLineIdBindingOuttext;
    }

    public void setProdSrNumDataLoadBinding(RichInputText prodSrNumDataLoadBinding) {
        this.prodSrNumDataLoadBinding = prodSrNumDataLoadBinding;
    }

    public RichInputText getProdSrNumDataLoadBinding() {
        return prodSrNumDataLoadBinding;
    }

    public void setFixedTagDataLoadBinding(RichInputListOfValues fixedTagDataLoadBinding) {
        this.fixedTagDataLoadBinding = fixedTagDataLoadBinding;
    }

    public RichInputListOfValues getFixedTagDataLoadBinding() {
        return fixedTagDataLoadBinding;
    }

    public void setProdLocationDataLoadBinding(RichInputListOfValues prodLocationDataLoadBinding) {
        this.prodLocationDataLoadBinding = prodLocationDataLoadBinding;
    }

    public RichInputListOfValues getProdLocationDataLoadBinding() {
        return prodLocationDataLoadBinding;
    }

    public void setProdUserDataLoadBinding(RichInputListOfValues prodUserDataLoadBinding) {
        this.prodUserDataLoadBinding = prodUserDataLoadBinding;
    }

    public RichInputListOfValues getProdUserDataLoadBinding() {
        return prodUserDataLoadBinding;
    }

    public void setProdSubLocDataLoadBinding(RichInputListOfValues prodSubLocDataLoadBinding) {
        this.prodSubLocDataLoadBinding = prodSubLocDataLoadBinding;
    }

    public RichInputListOfValues getProdSubLocDataLoadBinding() {
        return prodSubLocDataLoadBinding;
    }

    public void fixedAssetTagDataLoaderValueChangeListener(ValueChangeEvent valueChangeEvent) { //DataLoader level fixed asset tag lov value change event code
        try {
            String newValue = valueChangeEvent.getNewValue().toString().trim();
            System.out.println("New Value: " + newValue);
            // method for checking duplicate fixed asset tag exists or not
            fixedAssetTagDuplicateCheck( newValue, fixedTagDataLoadBinding, appM.getMnjWarrantyManagementHeaderVOEx2_1());
            
            fixedAssetTagDuplicateCheckLineLevel(newValue, fixedTagDataLoadBinding, appM.getMnjWarrantyManagementLineVOEx1());
            
        } catch (Exception e) {
            showMessage(e.toString(), "warn");
            e.printStackTrace();
        }
    }
    
    /**
     * Method for finding duplicate fixed asset tag
     * @param newValue
     * @param inputField
     * @param vo
     */
    public void fixedAssetTagDuplicateCheck(String newValue, RichInputListOfValues inputField, ViewObjectImpl vo){
        System.out.println("enters fixedAssetTag DuplicateCheck header level..........");
        try {
            Row[] rowArray = vo.getAllRowsInRange();
            System.out.println("    Total Records: " + rowArray.length);
            String rowWiseFixedTag;
            
            for(Row row : rowArray){
                System.out.println("  in header loop.....");
                try {
                    rowWiseFixedTag = row.getAttribute("FixedAssetTagId").toString().trim();
                } catch (Exception e) {
                    rowWiseFixedTag = null;
                }
                
                
                
                    
                if (newValue.equals(rowWiseFixedTag)){
                
                    System.out.println("Duplicate Header Fixed asset tag: " + rowWiseFixedTag);
                        
                    inputField.setValue(null);
                    
                    AdfFacesContext.getCurrentInstance().addPartialTarget(inputField);
                    
                    warnInputListOfValues(inputField, "Found Duplicate Fixed Asset Tag/N!");
                    break;
                }
            }
            
            
        } catch (Exception e) {
            showMessage(e.toString(), "warn");
            e.printStackTrace();
        }
            
            
            
    } 
    
    /**
     * Method for finding duplicate fixed asset tag LINE LEVEL
     * @param newValue
     * @param inputField
     * @param vo
     * @param voLine
     */
    public void fixedAssetTagDuplicateCheckLineLevel(String newValue, RichInputListOfValues inputField, ViewObjectImpl voLine){
        System.out.println("enters fixedAssetTag DuplicateCheck line level..........");
        try {
            Row[] rowArray = voLine.getAllRowsInRange();
            System.out.println("   Total Records: " + rowArray.length);
            String rowWiseFixedTag;
            
            for(Row row : rowArray){
                System.out.println("  in line loop.....");
                try {
                    rowWiseFixedTag = row.getAttribute("FixedAssetTagId").toString().trim();
                } catch (Exception e) {
                    rowWiseFixedTag = null;
                }
                
                
                    
                if (newValue.equals(rowWiseFixedTag)){
                
                    System.out.println("Duplicate Line Fixed asset tag: " + rowWiseFixedTag);
                        
                    inputField.setValue(null);
                    
                    AdfFacesContext.getCurrentInstance().addPartialTarget(inputField);
                    
                    warnInputListOfValues(inputField, "Found Duplicate Fixed Asset Tag/N!");
                    break;
                }
            }
            
            
        } catch (Exception e) {
            showMessage(e.toString(), "warn");
            e.printStackTrace();
        }
            
            
            
    } 
    
    

//    public void fixedAssetTagInputField(ValueChangeEvent valueChangeEvent) {
//        try {
//            String newValue = valueChangeEvent.getNewValue().toString().trim();
//            System.out.println("New Value: " + newValue);
//            fixedAssetTagDuplicateCheck( newValue, fixedAssetTagLOV, appM.getMnjWarrantyManagementHeaderVO1(), appM.getMnjWarrantyManagementLineVO1(), valueChangeEvent); // method for checking duplicate fixed asset tag exists or not
//        } catch (Exception e) {
//            showMessage(e.toString(), "warn");
//            e.printStackTrace();
//        }
//    }

    public void fixedAssetTagHeaderInputLOVValueChangeListener(ValueChangeEvent valueChangeEvent) { //Header level fixed asset tag lov value change event code
        try {
            String newValue = valueChangeEvent.getNewValue().toString().trim();
            System.out.println("New Value: " + newValue);
             // fixedAssetTagDuplicateCheck() method for checking duplicate fixed asset tag exists or not
          fixedAssetTagDuplicateCheck( newValue, fixedAssetTagHeaderInputLOV, appM.getMnjWarrantyManagementHeaderVOEx2_1() );
          fixedAssetTagDuplicateCheckLineLevel(newValue, fixedAssetTagHeaderInputLOV,  appM.getMnjWarrantyManagementLineVOEx1());
            
        } catch (Exception e) {
            showMessage(e.toString(), "warn");
            e.printStackTrace();
        }
    }

    public void setFixedAssetTagHeaderInputLOV(RichInputListOfValues fixedAssetTagHeaderInputLOV) {
        this.fixedAssetTagHeaderInputLOV = fixedAssetTagHeaderInputLOV;
    }

    public RichInputListOfValues getFixedAssetTagHeaderInputLOV() {
        return fixedAssetTagHeaderInputLOV;
    }

    public void setSetParentItemPopUpCheckBox(RichSelectBooleanCheckbox setParentItemPopUpCheckBox) {
        this.setParentItemPopUpCheckBox = setParentItemPopUpCheckBox;
    }

    public RichSelectBooleanCheckbox getSetParentItemPopUpCheckBox() {
        return setParentItemPopUpCheckBox;
    }

    public void setParentItemDialogListener(DialogEvent dialogEvent) {
        System.out.println("Enter in Dialog SetParentItem in Warranty Header....");
        try {
            if (dialogEvent.getOutcome().name().equals("yes")){
                System.out.println("enter in yes 3.......");
                ViewObject vo = appM.getMnjWarrantyManagementHeaderVO1();
                ViewObject voex = appM.getMnjWarrantyManagementHeaderVOEx1();
                ViewObject voLine = appM.getMnjWarrantyManagementLineVO1();
//                String headerId = vo.getCurrentRow().getAttribute("HeaderId").toString();
//                String headerWarTag = vo.getCurrentRow().getAttribute("WarrantyTag").toString();
//                System.out.println("headerId: " + headerId + ", headerWarTag: " +  headerWarTag);
//                
//                String headerIdex = voex.getCurrentRow().getAttribute("HeaderId").toString();
//                String headerWarTagex = voex.getCurrentRow().getAttribute("WarrantyTag").toString();
//                System.out.println("headerIdex: " + headerIdex + ", headerWarTagex: " +  headerWarTagex);
                
                
                insertInTable(vo, voex, voLine);
                refreshAllVO();
            }
            
            else if (dialogEvent.getOutcome().name().equals("no")){
                System.out.println("enter in no 3......");
            
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
    }
    
    public void insertInTable(ViewObject vo, ViewObject voex, ViewObject voLine){
        
        try {
            Row createdLineRow = voLine.createRow();
            
            //inserting in header table
            vo.getCurrentRow().setAttribute("ParentItemWarrantyTag", voex.getCurrentRow().getAttribute("WarrantyTag"));
            Row headerTableRowForChildInput = insertChildItemFlag(vo, voex); //method for inserting child flag in proper row in header table
            headerTableRowForChildInput.setAttribute("ChildExistsFlag", "Yes");
            
            //inserting in line table
            createdLineRow.setAttribute("HeaderId", voex.getCurrentRow().getAttribute("HeaderId"));
            createdLineRow.setAttribute("Organization", vo.getCurrentRow().getAttribute("Organization"));
            createdLineRow.setAttribute("OrgId", vo.getCurrentRow().getAttribute("OrgId"));
            createdLineRow.setAttribute("WarrantyTag", vo.getCurrentRow().getAttribute("WarrantyTag"));
            createdLineRow.setAttribute("ProductSerialNo", vo.getCurrentRow().getAttribute("ProductSerialNo"));
            createdLineRow.setAttribute("FixedAssetTagId", vo.getCurrentRow().getAttribute("FixedAssetTagId"));

            createdLineRow.setAttribute("WarrantyMonths", vo.getCurrentRow().getAttribute("WarrantyMonths"));
            createdLineRow.setAttribute("Grn", vo.getCurrentRow().getAttribute("Grn"));
            createdLineRow.setAttribute("GrnDate", vo.getCurrentRow().getAttribute("GrnDate"));
            createdLineRow.setAttribute("SupplierName", vo.getCurrentRow().getAttribute("SupplierName"));
            createdLineRow.setAttribute("Spo", vo.getCurrentRow().getAttribute("Spo"));
            createdLineRow.setAttribute("ItemId", vo.getCurrentRow().getAttribute("ItemId"));
            createdLineRow.setAttribute("ItemDescription", vo.getCurrentRow().getAttribute("ItemDescription"));
            createdLineRow.setAttribute("Location", vo.getCurrentRow().getAttribute("Location"));
            createdLineRow.setAttribute("ProductUser", vo.getCurrentRow().getAttribute("ProductUser"));
            createdLineRow.setAttribute("VendorId", vo.getCurrentRow().getAttribute("VendorId"));
            createdLineRow.setAttribute("PoLineId", vo.getCurrentRow().getAttribute("PoLineId"));
            createdLineRow.setAttribute("PoHeaderId", vo.getCurrentRow().getAttribute("PoHeaderId"));
            createdLineRow.setAttribute("ItemCategory", vo.getCurrentRow().getAttribute("ItemCategory"));
            createdLineRow.setAttribute("ItemSubCategory", vo.getCurrentRow().getAttribute("ItemSubCategory"));
            createdLineRow.setAttribute("ItemCategoryShort", vo.getCurrentRow().getAttribute("ItemCategoryShort"));
            createdLineRow.setAttribute("ItemCategoryId", vo.getCurrentRow().getAttribute("ItemCategoryId"));
            createdLineRow.setAttribute("ItemSubCategoryId", vo.getCurrentRow().getAttribute("ItemSubCategoryId"));
            createdLineRow.setAttribute("ItemSubCategoryShort", vo.getCurrentRow().getAttribute("ItemSubCategoryShort"));
            createdLineRow.setAttribute("SubLocation", vo.getCurrentRow().getAttribute("SubLocation"));
            createdLineRow.setAttribute("WarrantyType", vo.getCurrentRow().getAttribute("WarrantyType"));
            createdLineRow.setAttribute("Qty", vo.getCurrentRow().getAttribute("Qty"));
            createdLineRow.setAttribute("Uom", vo.getCurrentRow().getAttribute("Uom"));
           
            
            voLine.insertRow(createdLineRow);
            
            appM.getDBTransaction().commit();
            
            voex.executeQuery();
            AdfFacesContext.getCurrentInstance().addPartialTarget(mnjWarrantyHeaderTable);
                
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
    }
    
    public Row  insertChildItemFlag(ViewObject vo, ViewObject voex){
        try {
            Row[] rowArray = vo.getAllRowsInRange();
            for (Row currRow: rowArray){
                if (currRow.getAttribute("HeaderId") == voex.getCurrentRow().getAttribute("HeaderId")){
                    System.out.println("enter header_id equals.....");
                    return currRow;
                }
                
            }
            
        } catch (Exception e) {
           
            e.printStackTrace();
        }
        return null;
    }

    public void setWarrantyLineTable(RichTable warrantyLineTable) {
        this.warrantyLineTable = warrantyLineTable;
    }

    public RichTable getWarrantyLineTable() {
        return warrantyLineTable;
    }

    public void editPopUpFetchListSetParentItem(PopupFetchEvent popupFetchEvent) {
        try {
            ViewObject vo = appM.getMnjWarrantyManagementHeaderVOEx1();
            ViewObject voHeader = appM.getMnjWarrantyManagementHeaderVO1();
            
            vo.setWhereClause("header_id not in (" + voHeader.getCurrentRow().getAttribute("HeaderId") +")");
            
            vo.executeQuery();
        } catch (Exception e) {
            
            e.printStackTrace();
        }
       
    }

    public void editDialogListenerSetParentItemInsideFillWarrantyInfo(DialogEvent dialogEvent) {
        System.out.println("Enter in Dialog SetParentItem in InsideFillWarrantyInfo....");
        try {
            
            if (dialogEvent.getOutcome().name().equals("yes")){
                System.out.println("enter in yes 4.......");
                ViewObject vo = appM.getMnjWarrantyManagementHeaderVO1();
                ViewObject voex = appM.getMnjWarrantyManagementHeaderVOExSetParent1();
                ViewObject voLine = appM.getMnjWarrantyManagementLineVO1();
                ViewObject voDataLoaderSetParent = appM.getMnjWarrDataLoaderSetParVO1();
                
                String headerId = vo.getCurrentRow().getAttribute("HeaderId").toString();
                String headerWarTag = vo.getCurrentRow().getAttribute("WarrantyTag").toString();
                System.out.println("headerId: " + headerId + ", headerWarTag: " +  headerWarTag);

                String headerIdex = voex.getCurrentRow().getAttribute("HeaderId").toString();
                String headerWarTagex = voex.getCurrentRow().getAttribute("WarrantyTag").toString();
                System.out.println("headerIdex: " + headerIdex + ", headerWarTagex: " +  headerWarTagex);
                
                insertInDataLoaderSetParent(voDataLoaderSetParent, voex);
//                appM.getDBTransaction().commit();
            //            refreshAllVO();
            }
            
            else if (dialogEvent.getOutcome().name().equals("no")){
                System.out.println("enter in no 4......");
            
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
        
        
    }

    public void setParentItemInputFieldBinding(RichInputText parentItemInputFieldBinding) {
        this.parentItemInputFieldBinding = parentItemInputFieldBinding;
    }

    public RichInputText getParentItemInputFieldBinding() {
        return parentItemInputFieldBinding;
    }

    public void editPopUpFetchListenerSetParentItemInFillWarrantyInfo(PopupFetchEvent popupFetchEvent) {
        ViewObject vo = appM.getMnjWarrantyManagementHeaderVOExSetParent1();
//        
        vo.executeQuery();
    }
    
    public void removeTableData(ViewObject vo) { //unused
        System.out.println(">>>>>>>>>>>>>>>>> Enter in removeTableData.............");
        
//        Row[] rowArray = vo.getAllRowsInRange();
//        for(Row row : rowArray) {
//            System.out.println(">>>>>>>>> enter in delete row....");
//            row.remove();
//        }
        
        
        
        try {
            RowSetIterator it = vo.createRowSetIterator("ss");
                while(it.hasNext()){
                    System.out.println("XXXXXXXXXX enter delete");
                    Row row = it.next();
                    row.remove();
                }
            it.closeRowSetIterator();
            
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        
       
    }
    
    public void insertInDataLoaderSetParent(ViewObject voDataLoaderSetParent, ViewObject voex){
        System.out.println("Enter in insertInDataLoaderSetParent.....");
        try {
            Row createdLineRow = voDataLoaderSetParent.createRow();
            
            //inserting in dataLoaderTableSetParent
            createdLineRow.setAttribute("HeaderId", voex.getCurrentRow().getAttribute("HeaderId"));
            createdLineRow.setAttribute("OrgId", voex.getCurrentRow().getAttribute("OrgId"));
            createdLineRow.setAttribute("Organization", voex.getCurrentRow().getAttribute("Organization"));
            createdLineRow.setAttribute("Grn", voex.getCurrentRow().getAttribute("Grn"));
            createdLineRow.setAttribute("GrnDate", voex.getCurrentRow().getAttribute("GrnDate"));
            createdLineRow.setAttribute("Qty", voex.getCurrentRow().getAttribute("Qty"));
            createdLineRow.setAttribute("Uom", voex.getCurrentRow().getAttribute("Uom"));
            createdLineRow.setAttribute("Spo", voex.getCurrentRow().getAttribute("Spo"));
            createdLineRow.setAttribute("SupplierName", voex.getCurrentRow().getAttribute("SupplierName"));
            createdLineRow.setAttribute("ItemId", voex.getCurrentRow().getAttribute("ItemId"));
            createdLineRow.setAttribute("ItemDescription", voex.getCurrentRow().getAttribute("ItemDescription"));
            createdLineRow.setAttribute("VendorId", voex.getCurrentRow().getAttribute("VendorId"));
            createdLineRow.setAttribute("PoLineId", voex.getCurrentRow().getAttribute("PoLineId"));
            createdLineRow.setAttribute("PoHeaderId", voex.getCurrentRow().getAttribute("PoHeaderId"));
            createdLineRow.setAttribute("ItemCategory", voex.getCurrentRow().getAttribute("ItemCategory"));
            createdLineRow.setAttribute("ItemSubCategory", voex.getCurrentRow().getAttribute("ItemSubCategory"));
            createdLineRow.setAttribute("ItemCategoryShort", voex.getCurrentRow().getAttribute("ItemCategoryShort"));
            createdLineRow.setAttribute("ItemCategoryId", voex.getCurrentRow().getAttribute("ItemCategoryId"));
            createdLineRow.setAttribute("ItemSubCategoryId", voex.getCurrentRow().getAttribute("ItemSubCategoryId"));
            createdLineRow.setAttribute("ItemSubCategoryShort", voex.getCurrentRow().getAttribute("ItemSubCategoryShort"));
            
            createdLineRow.setAttribute("WarrantyTag", voex.getCurrentRow().getAttribute("WarrantyTag"));
            createdLineRow.setAttribute("WarrantyType", voex.getCurrentRow().getAttribute("WarrantyType"));
            createdLineRow.setAttribute("WarrantyMonths", voex.getCurrentRow().getAttribute("WarrantyMonths"));
            createdLineRow.setAttribute("ProductSerialNo", voex.getCurrentRow().getAttribute("ProductSerialNo"));
            createdLineRow.setAttribute("FixedAssetTagId", voex.getCurrentRow().getAttribute("FixedAssetTagId"));
            createdLineRow.setAttribute("CostCenterName", voex.getCurrentRow().getAttribute("CostCenterName"));
            createdLineRow.setAttribute("CostCenter", voex.getCurrentRow().getAttribute("CostCenter"));
            createdLineRow.setAttribute("Location", voex.getCurrentRow().getAttribute("Location"));
            createdLineRow.setAttribute("ProductUser", voex.getCurrentRow().getAttribute("ProductUser"));
            createdLineRow.setAttribute("SubLocation", voex.getCurrentRow().getAttribute("SubLocation"));
            
            parentItemInputFieldBinding.setValue(voex.getCurrentRow().getAttribute("WarrantyTag"));
            AdfFacesContext.getCurrentInstance().addPartialTarget(parentItemInputFieldBinding);
            
            voDataLoaderSetParent.insertRow(createdLineRow);
            
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }

    public void setParentWarrantyTagInputBind(RichOutputText parentWarrantyTagInputBind) {
        this.parentWarrantyTagInputBind = parentWarrantyTagInputBind;
    }

    public RichOutputText getParentWarrantyTagInputBind() {
        return parentWarrantyTagInputBind;
    }
    
    public void insertInLineVo(ViewObject voLine, ViewObject voDataloaderSetPar, ViewObject voDataLoaderWarrantyInfo, ViewObject voHeader, ViewObject voWarrGrn ){
        System.out.println("Enter in Insert In Line VO....");
        Row createdRow = voLine.createRow();
        System.out.println(">>>> HeaderId: " + voDataloaderSetPar.getCurrentRow().getAttribute("HeaderId"));
        //insert in header
        voHeader.setRangeSize(32750);
        Row[] rowArray = voHeader.getAllRowsInRange();
        
        for (Row row : rowArray){ 
            if (row.getAttribute("HeaderId") == voDataloaderSetPar.getCurrentRow().getAttribute("HeaderId")){
                System.out.println("HeaderId Matches, now putting child exists 'Yes' in header Row...........");
                row.setAttribute("ChildExistsFlag", "Yes");
            }
        }
        
        //insert in line
        createdRow.setAttribute("HeaderId", voDataloaderSetPar.getCurrentRow().getAttribute("HeaderId")); //this line is creating foreign key in line vo
        createdRow.setAttribute("OrgId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("Ou"));
        createdRow.setAttribute("Organization", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("OrganizationCode"));
        createdRow.setAttribute("SupplierName", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("Supplier"));
        createdRow.setAttribute("Spo", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoNumber"));
        createdRow.setAttribute("ItemId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("ItemId"));
        createdRow.setAttribute("ItemDescription", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("ItemDescription"));
        createdRow.setAttribute("Grn", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnNumber"));
        createdRow.setAttribute("GrnDate", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnDate"));
        createdRow.setAttribute("Qty", 1);
        createdRow.setAttribute("Uom", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnOum"));
        createdRow.setAttribute("PoLineId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoLineId"));
        createdRow.setAttribute("VendorId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("VendorId"));
        createdRow.setAttribute("PoHeaderId", appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("PoHeaderId"));
        
//        createdRow.setAttribute("ItemCategory", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemCategory"));
//        createdRow.setAttribute("ItemCategoryId", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemCategoryId"));
        createdRow.setAttribute("ItemCategoryShort", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemCategoryShort"));
//        createdRow.setAttribute("ItemSubCategory", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemSubCategory"));
//        createdRow.setAttribute("ItemSubCategoryId", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemSubCategoryId"));
        createdRow.setAttribute("ItemSubCategoryShort", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemSubCategoryShort"));
        createdRow.setAttribute("WarrantyTagYear", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyTagYear"));
        createdRow.setAttribute("WarrantyType", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyType"));
        createdRow.setAttribute("WarrantyMonths", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyMonths"));
        System.out.println("Product S/N: " + appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ProductSerialNo"));
        createdRow.setAttribute("ProductSerialNo", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ProductSerialNo"));
        createdRow.setAttribute("FixedAssetTagId", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("FixedAssetTagId"));
        createdRow.setAttribute("Location", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("Location"));
        createdRow.setAttribute("CostCenterName", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("CostCenterName"));
        createdRow.setAttribute("CostCenter", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("CostCenter"));
        createdRow.setAttribute("ProductUser", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ProductUser"));
        createdRow.setAttribute("ProductUserEmpId", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ProductUserEmpId"));
        createdRow.setAttribute("ProductUserDept", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ProductUserDept"));
        createdRow.setAttribute("SubLocation", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("SubLocation"));
        createdRow.setAttribute("Remarks", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("Remarks"));
        createdRow.setAttribute("ScheduleMaintainFlag", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ScheduleMaintainFlag"));
        createdRow.setAttribute("MaintainPeriod", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("MaintainPeriod"));
        createdRow.setAttribute("NextMaintainDate", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("NextMaintainDate"));
        createdRow.setAttribute("WarrantyStartDate", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyStartDate"));
        createdRow.setAttribute("WarrantyEndDate", appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyEndDate"));
        
        String unit = getOperatingUnitBinding().getValue().toString();
        String grnNumber = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("GrnNumber").toString();
        String itemId = appM.getWarrantyGRNVO1().getCurrentRow().getAttribute("ItemId").toString();
        orgName = getOrganizationNameBinding().getValue().toString();
        majorCat = appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemCategoryShort").toString();
        minorCat = appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("ItemSubCategoryShort").toString();
        tagYear =  appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyTagYear").toString();
        createdRow.setAttribute("WarrantyItemSerialNumber", Integer.parseInt(getSrNoStringLine(orgName, majorCat, minorCat, tagYear)));
        createdRow.setAttribute("WarrantyTag", generateWarrantyTag(orgName, majorCat, minorCat, tagYear, getSrNoStringLine(orgName, majorCat, minorCat, tagYear)));
        voLine.insertRow(createdRow);
    }

    public void setItemIdBinding(RichOutputText itemIdBinding) {
        this.itemIdBinding = itemIdBinding;
    }

    public RichOutputText getItemIdBinding() {
        return itemIdBinding;
    }

    public void valueChangeListenerFixedAssetTagInLine(ValueChangeEvent valueChangeEvent) { //Line level fixed asset tag lov value change event code
        try {
            String newValue = valueChangeEvent.getNewValue().toString().trim();
            System.out.println("Fixed Asset Tag New Value: " + newValue);
            // method for checking duplicate fixed asset tag exists or not
            fixedAssetTagDuplicateCheck( newValue, fixedAssetTagLineLOV, appM.getMnjWarrantyManagementHeaderVOEx2_1()); 
            fixedAssetTagDuplicateCheckLineLevel(newValue, fixedAssetTagLineLOV, appM.getMnjWarrantyManagementLineVOEx1());
        } catch (Exception e) {
            showMessage(e.toString(), "warn");
            e.printStackTrace();
        }
    }

    public void setFixedAssetTagLineLOV(RichInputListOfValues fixedAssetTagLineLOV) {
        this.fixedAssetTagLineLOV = fixedAssetTagLineLOV;
    }

    public RichInputListOfValues getFixedAssetTagLineLOV() {
        return fixedAssetTagLineLOV;
    }

    public void clearParentActionListener(ActionEvent actionEvent) {
        System.out.println("enters in clear Parent......");
        try {
            parentItemInputFieldBinding.setValue(null);
            AdfFacesContext.getCurrentInstance().addPartialTarget(parentItemInputFieldBinding);
            
        } catch (Exception e) {
 
            e.printStackTrace();
        }
        
    }

    public void setWarrantyPeriodInMonthsBind(RichOutputText warrantyPeriodInMonthsBind) {
        this.warrantyPeriodInMonthsBind = warrantyPeriodInMonthsBind;
    }

    public RichOutputText getWarrantyPeriodInMonthsBind() {
        return warrantyPeriodInMonthsBind;
    }

    public void setWarrantyMonthsInputText2(RichInputText warrantyMonthsInputText2) {
        this.warrantyMonthsInputText2 = warrantyMonthsInputText2;
    }

    public RichInputText getWarrantyMonthsInputText2() {
        return warrantyMonthsInputText2;
    }

    public void customDelete(ActionEvent actionEvent) {
        System.out.println("enter in custom Delete.....");
        ViewObject vo = appM.getMnjWarrantyManagementLineVO1();
        ViewObject vo1 = appM.getMnjWarrantyManagementHeaderVO1();
        
        vo.getCurrentRow().remove();
        
        vo.executeQuery();
        vo1.executeQuery();
        AdfFacesContext.getCurrentInstance().addPartialTarget(mnjWarrantyHeaderTable);
    }

    public void editPopUpFetchListenerSetSubItems(PopupFetchEvent popupFetchEvent) {
        ViewObject vo = appM.getMnjWarrantyManagementHeaderVOEx1();
        vo.executeQuery();
    }

    public void flagScheduleMaintainValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        try {
            System.out.println("Changed Value: " + valueChangeEvent.getNewValue());
            
//            if (valueChangeEvent.getNewValue().equals("") ){
//                System.out.println("into false");
//            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMaintainFlag(RichSelectBooleanCheckbox maintainFlag) {
        this.maintainFlag = maintainFlag;
    }

    public RichSelectBooleanCheckbox getMaintainFlag() {
        return maintainFlag;
    }

    public void maintainPeriodValueChangeEvent(ValueChangeEvent valueChangeEvent) { 
        //Value Change Event inside Data Loader 
        String month = null;
        try {
            System.out.println("Changed Value: " + valueChangeEvent.getNewValue());
            try {
                month = valueChangeEvent.getNewValue().toString();
            } catch (Exception e) {
                month = "0";
            }
            if ( month == null || month == "" || month.equals("")){
                warnInputText(maintainPeriod, "Please Enter Maintain Period!");
            }else{
                //adding months from Warranty Start Date
                SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().getAttribute("WarrantyStartDate").toString()));
                cal.add(Calendar.MONTH, Integer.parseInt(month));
                System.out.println("Calculated Date: " + cal.getTime());
        //                        nextMaintainDate.setValue(castToJBODate(cal.getTime().toString()));
                appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().setAttribute("NextMaintainDate", cal.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public void setMaintainPeriod(RichInputText maintainPeriod) {
        this.maintainPeriod = maintainPeriod;
    }

    public RichInputText getMaintainPeriod() {
        return maintainPeriod;
    }

    public void deleteAllDataLoader(ActionEvent actionEvent) {
        try {
            ViewObject vo = appM.getMnjWarrantyDataLoaderVO1();
            vo.setRangeSize(32750);
            Row rowArray[] = vo.getAllRowsInRange();
            System.out.println("total entries: " + rowArray.length);
            for (Row eachRow: rowArray){
                eachRow.remove();
            }
            vo.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMaintainPeriodCapture(RichInputText maintainPeriodCapture) {
        this.maintainPeriodCapture = maintainPeriodCapture;
    }

    public RichInputText getMaintainPeriodCapture() {
        return maintainPeriodCapture;
    }

    public void setNextMaintainDate(RichInputDate nextMaintainDate) {
        this.nextMaintainDate = nextMaintainDate;
    }

    public RichInputDate getNextMaintainDate() {
        return nextMaintainDate;
    }

    public void maintainPeriodInputCaptureValueChangeEvent(ValueChangeEvent valueChangeEvent) {
                String month = null;
                try {
                    System.out.println("Changed Value: " + valueChangeEvent.getNewValue());
                    try {
                        month = valueChangeEvent.getNewValue().toString();
                    } catch (Exception e) {
                        month = "0";
                    }
                    if ( month == null || month == "" || month.equals("")){
                        warnInputText(maintainPeriodCapture, "Please Enter Maintain Period!");
                    }else{
                        //adding months from Warranty Start Date
                        SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dateFormat.parse(warrantyStartDate.getValue().toString()));
                        cal.add(Calendar.MONTH, Integer.parseInt(month));
                        System.out.println("Calculated Date: " + cal.getTime());
//                        nextMaintainDate.setValue(castToJBODate(cal.getTime().toString()));
                        appM.getWarrantyGRNVO1().getCurrentRow().setAttribute("NextMaintainDate", cal.getTime());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }        
   
    }
    
    public void setMaintainPeriodLine(RichInputText maintainPeriodLine) {
        this.maintainPeriodLine = maintainPeriodLine;
    }

    public RichInputText getMaintainPeriodLine() {
        return maintainPeriodLine;
    }

    public void mainPerHeadValChanList(ValueChangeEvent valueChangeEvent) {
        String month = null;
        try {
            System.out.println("Changed Value: " + valueChangeEvent.getNewValue());
            try {
                month = valueChangeEvent.getNewValue().toString();
            } catch (Exception e) {
                month = "0";
            }
            if ( month == null || month == "" || month.equals("")){
                warnInputText(maintainPeriodHeader, "Please Enter Maintain Period!");
            }else{
                //adding months from Warranty Start Date
                SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(appM.getMnjWarrantyManagementHeaderVO1().getCurrentRow().getAttribute("WarrantyStartDate").toString()));
                cal.add(Calendar.MONTH, Integer.parseInt(month));
                System.out.println("Calculated Date: " + cal.getTime());
//                        nextMaintainDate.setValue(castToJBODate(cal.getTime().toString()));
                appM.getMnjWarrantyManagementHeaderVO1().getCurrentRow().setAttribute("NextMaintainDate", cal.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public void setMaintainPeriodHeader(RichInputText maintainPeriodHeader) {
        this.maintainPeriodHeader = maintainPeriodHeader;
    }

    public RichInputText getMaintainPeriodHeader() {
        return maintainPeriodHeader;
    }

    

    public void mainPerLineValChanLis(ValueChangeEvent valueChangeEvent) {
        String month = null;
        try {
            System.out.println("Changed Value: " + valueChangeEvent.getNewValue());
            try {
                month = valueChangeEvent.getNewValue().toString();
            } catch (Exception e) {
                month = "0";
            }
            if ( month == null || month == "" || month.equals("")){
                warnInputText(maintainPeriodLine, "Please Enter Maintain Period!");
            }else{
                //adding months from Warranty Start Date
                SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(appM.getMnjWarrantyManagementLineVO1().getCurrentRow().getAttribute("WarrantyStartDate").toString()));
                cal.add(Calendar.MONTH, Integer.parseInt(month));
                System.out.println("Calculated Date: " + cal.getTime());
//                        nextMaintainDate.setValue(castToJBODate(cal.getTime().toString()));
                appM.getMnjWarrantyManagementLineVO1().getCurrentRow().setAttribute("NextMaintainDate", cal.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
}

    public void callScheduleEnable(ActionEvent actionEvent) {
        try {
            scheduleFlagEnable(appM.getMnjWarrantyManagementHeaderVO1());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method is for enable schedule flag from N to Y
     * @param vo
     */
    public void scheduleFlagEnable(ViewObject vo){
        try {
            vo.getCurrentRow().setAttribute("ScheduleMaintainFlag", "Y");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callScheduleEnableLine(ActionEvent actionEvent) {
        try {
            scheduleFlagEnable(appM.getMnjWarrantyManagementLineVO1());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scheduleMaintenanceHistoryHeadPopUpEvent(PopupFetchEvent popupFetchEvent) {
        try {
            ViewObject vo = appM.getWarrHeaderScheduleHistoryVO1();
            vo.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void popUpEventScheduleMaintenanceHistorySubItem(PopupFetchEvent popupFetchEvent) {
        try {
            ViewObject vo = appM.getWarrLineScheduleHistoryVO1();
            vo.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWarrantyStartDate(RichInputDate warrantyStartDate) {
        this.warrantyStartDate = warrantyStartDate;
    }

    public RichInputDate getWarrantyStartDate() {
        return warrantyStartDate;
    }

    public void setWarrantyExpiryDate(RichInputDate warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
//        this.warrantyExpiryDate = 
    }

    public RichInputDate getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public void setGrnDate(RichOutputText grnDate) {
        this.grnDate = grnDate;
    }

    public RichOutputText getGrnDate() {
        return grnDate;
    }

    public void warrantyStartDateValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        try {
            System.out.println("Changed Date: " + valueChangeEvent.getNewValue());
//            setWarrantyExpiryDate(); //calling method overloading
            setNextMaintainDate(valueChangeEvent.getNewValue().toString(), "InsideCapture"); //calling method overloading
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void warrantyPeriodMonthsValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        try {
            System.out.println("Changed Period Months: " + valueChangeEvent.getNewValue());
            setWarrantyExpiryDate(Integer.parseInt(valueChangeEvent.getNewValue().toString())); //calling method overloading
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * Converts a String to oracle.jbo.domain.Date
     * @param String 
     * @param String 
     * @return oracle.jbo.domain.Date
     *
     */
    
    public oracle.jbo.domain.Date castToJBODate(String aDate, String dateFormatPattern){
        
       
        
        java.util.Date date;
        
            if (aDate != null){
                
                try {
                    
                    SimpleDateFormat  formatter = new SimpleDateFormat(dateFormatPattern);
                    date = formatter.parse(aDate);
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                    oracle.jbo.domain.Date jboDate = new oracle.jbo.domain.Date(sqlDate);
                    
                    System.out.println("### Date: " + jboDate);
                    return jboDate;
                } catch (Exception e) {
                
                e.printStackTrace();
                }
            }
        return null;
    }

    public void warrStartDateDataLoadValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        try {
            System.out.println("Changed Date: " + valueChangeEvent.getNewValue());
            //calling method overloading
            setWarrantyExpiryDate(appM.getMnjWarrantyDataLoaderVO1(), valueChangeEvent.getNewValue().toString());
            //calling method overloading
            setNextMaintainDate("InsideDataLoader");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void warrPeriodMonthsDataLoadValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        try {
            System.out.println("Changed Date: " + valueChangeEvent.getNewValue());
            setWarrantyExpiryDate(appM.getMnjWarrantyDataLoaderVO1(), valueChangeEvent); //calling method overloading
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWarrStrtDateDataLoader(RichInputDate warrStrtDateDataLoader) {
        this.warrStrtDateDataLoader = warrStrtDateDataLoader;
    }

    public RichInputDate getWarrStrtDateDataLoader() {
        return warrStrtDateDataLoader;
    }

    public void setWarrPeriodMonthsDataLoader(RichInputText warrPeriodMonthsDataLoader) {
        this.warrPeriodMonthsDataLoader = warrPeriodMonthsDataLoader;
    }

    public RichInputText getWarrPeriodMonthsDataLoader() {
        return warrPeriodMonthsDataLoader;
    }

    public void setWarrExpiryDateDataLoader(RichInputDate warrExpiryDateDataLoader) {
        this.warrExpiryDateDataLoader = warrExpiryDateDataLoader;
    }

    public RichInputDate getWarrExpiryDateDataLoader() {
        return warrExpiryDateDataLoader;
    }
    
    /**
     * method overloading
     * @param insideDataLoader
     */
    public void setNextMaintainDate(String insideDataLoader){
        try {
            String month = null;
            try {
                month = getMaintainPeriod().getValue().toString();
            } catch (Exception e) {
                ;
            }
            if ( month != null || month != "" || !month.equals("")){
                //adding months from Warranty Start Date
                SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(warrStrtDateDataLoader.getValue().toString()));
                cal.add(Calendar.MONTH, Integer.parseInt(month));
                System.out.println("Calculated Date: " + cal.getTime());
                appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().setAttribute("NextMaintainDate", cal.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }       
    }
    
   /**
     * method overloading
     * @param changedDate
     * @param insideCapture
     */
    public void setNextMaintainDate(String changedDate, String insideCapture){
        try {
            String month = null;
            try {
                month = getMaintainPeriodCapture().getValue().toString();
            } catch (Exception e) {
                ;
            }
            if ( month != null || month != "" || !month.equals("")){
                //adding months from Warranty Start Date
                SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(changedDate));
                cal.add(Calendar.MONTH, Integer.parseInt(month));
                System.out.println("Calculated Date: " + cal.getTime());
                appM.getWarrantyGRNVO1().getCurrentRow().setAttribute("NextMaintainDate", cal.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }       
    }

    public void callOKButton(ActionEvent actionEvent) {
        try {
            System.out.println("Enter in OK button...");

                if (!getGrnQtyBinding().getValue().toString().equals("0")){
                    ViewObject vo = appM.getMnjWarrantyDataLoaderVO1();
                    row = vo.getAllRowsInRange();
                    voLength = row.length;
                    System.out.println("enter in <======> grn qty  == 1 or grn qty > 1");
            //                        System.out.println("voLength: " + voLength);
                        if (voLength == 0){
                            System.out.println("enter in volength == 0, " + "voLength: " + voLength);
            //                              warnTable(dataLoaderTable, "No Data Found In Warranty Info Loader Table!");
//                            ResetUtils.reset(fillInfoPanelGroupLayoutBinding);

            //                                warrantyTypeLOVBinding.setValue("");
            
                            AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyTypeLOVBinding);
            
                            showMessage("Found No Generated Warranty Info!", "warn");
                            
                        }
                        else if (voLength > 0){
                            System.out.println("enter in volength > 0, " +"voLength: " + voLength);
                            System.out.println("WarrantyType: " + getWarrantyTypeLOVBinding().getValue());
                            System.out.println("Warranty Months: " + getWarrantyMonthsInputTextBind().getValue());

//                            System.out.println("Major Category: " + getMajorCategoryBinding().getValue());
//                            System.out.println("Minor Categroy: " + getSubCategoryBinding().getValue());
                            for (Row r : row ){
                                System.out.println("enter for-each loop 1...");
                                System.out.println("row product s/n: " + r.getAttribute("ProductSerialNo"));
                                if (r.getAttribute("ProductSerialNo") == null){
                                    warnInputText(prodSrNumDataLoadBinding, "Please Enter Product S/N!");
                                    
                                }
                                
            //                                    System.out.println("getAllProductSnValue() returns: " + getAllProductSnValue());
                                else if(getAllProductSnValue() == "allOK") {
                                    System.out.println("enter pop up condition!");
                                    RichPopup.PopupHints hints = new RichPopup.PopupHints();
                                    getSavePopUpBinding().show(hints);// pop up will appear
                                }
                            }
                            
                        }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callCancelButton(ActionEvent actionEvent) {
        try {
            System.out.println("Enter in Cancel button...");
            RichPopup popup = getFillWarrantyInfoPopUp();
            popup.cancel();
            System.out.println("popup terminated......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFillWarrantyInfoPopUp(RichPopup fillWarrantyInfoPopUp) {
        this.fillWarrantyInfoPopUp = fillWarrantyInfoPopUp;
    }

    public RichPopup getFillWarrantyInfoPopUp() {
        return fillWarrantyInfoPopUp;
    }

    public void callYesButtonAction(ActionEvent actionEvent) {
        try {
            System.out.println("Enter in yes button action...");
            ViewObject vo = appM.getMnjWarrantyManagementHeaderVO1();
            if (getGrnQtyBinding().getValue().toString().equals("1")){
                System.out.println("If Grn Qty == 1..........");
                int grnQty = Integer.parseInt(getGrnQtyBinding().getValue().toString()) ;
                insertInWarrantyHeader(vo, grnQty); // method for inserting data in MASTER_TABLE
                appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().remove();
                save();
                refreshAllVO();
            }else {
                System.out.println("If Grn Qty > 1.........");
                int grnQty = Integer.parseInt(getGrnQtyBinding().getValue().toString()) ;
                System.out.println("Grn Qty:" + grnQty);
                int iterator = 0;
                int i = 0 ;
                while (iterator < grnQty){ 
                    i = iterator + 1;
                    System.out.println("++++Row number: " + i);
                    insertInWarrantyHeader(vo, grnQty);// method for inserting data in MASTER_TABLE
                    appM.getMnjWarrantyDataLoaderVO1().getCurrentRow().remove(); 
                    appM.getDBTransaction().commit();
                    iterator++;
                }
                refreshAllVO(); 
            }
            closeSavePopUp(); 
            closeFillWarrantyPopUp();
        } catch (Exception e) {
            if (e.toString().contains("MNJ_WARRANTY_MANAGEMENT_H_U02")){
                showMessage("Found Duplicate Fixed Asset Tag/N!", "warn");
            } else {
                showMessage(e.toString(), "warn");
                e.printStackTrace();
            }
        }
    }

    public void callNoButtonAction(ActionEvent actionEvent) {
        System.out.println("enter in no button action.........");
        try {
            RichPopup popup = getSavePopUpBinding();
            popup.cancel();
            AdfFacesContext.getCurrentInstance().addPartialTarget(savePopUpBinding);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void closeSavePopUp(){
      try {
            RichPopup popup = getSavePopUpBinding();
            popup.cancel();
            AdfFacesContext.getCurrentInstance().addPartialTarget(savePopUpBinding);
      } catch (Exception e) {
            e.printStackTrace();
        }
    } 
               
    public void closeFillWarrantyPopUp(){
        try {
            RichPopup popup = getFillWarrantyInfoPopUp();
            popup.cancel();
            AdfFacesContext.getCurrentInstance().addPartialTarget(fillWarrantyInfoPopUp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getCalculatedExpiryDate(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(warrantyStartDate.getValue().toString()));
            //            if (warrantyMonthsInputTextBind.getValue().toString().equals("0")){
                cal.add(Calendar.MONTH, Integer.parseInt(warrantyMonthsInputText2.getValue().toString()));
            //            } else{
            //                cal.add(Calendar.MONTH, Integer.parseInt(warrantyMonthsInputTextBind.getValue().toString()));
            //            }
            System.out.println("Warranty Expiry Date before casting: " + cal.getTime());
            return cal.getTime().toString();
//            warrantyExpiryDate.setValue(castToJBODate(cal.getTime().toString(), "E MMM dd HH:mm:ss Z yyyy"));
//            System.out.println("Warranty Expiry Date after casting: " + warrantyExpiryDate.getValue());
//            AdfFacesContext.getCurrentInstance().addPartialTarget(warrantyExpiryDate); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void warrStartDateHeaderValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        try {
            System.out.println("Changed Date: " + valueChangeEvent.getNewValue());
            //calling method overloading
            setWarrantyExpiryDateHeader(appM.getMnjWarrantyManagementHeaderVO1(), valueChangeEvent.getNewValue().toString());
            //calling method overloading
            setNextMaintainDateHeader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setWarrantyExpiryDateHeader(ViewObject vo, String changedWarrStartDate) throws ParseException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(changedWarrStartDate));
            cal.add(Calendar.MONTH, Integer.parseInt(warrPeriodHeader.getValue().toString()));
            System.out.println("Warranty Expiry Date: " + cal.getTime());
            vo.getCurrentRow().setAttribute("WarrantyEndDate", cal.getTime());
            AdfFacesContext.getCurrentInstance().addPartialTarget(warrExpiryDateHeader); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * method overloading
     * @param changedWarrPeriodH
     * @throws ParseException
     */
    public void setWarrantyExpiryDateHeader(String changedWarrPeriodH) throws ParseException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(warrStartDateHeader.getValue().toString()));
            cal.add(Calendar.MONTH, Integer.parseInt(changedWarrPeriodH));
            System.out.println("Warranty Expiry Date: " + cal.getTime());
            appM.getMnjWarrantyManagementHeaderVO1().getCurrentRow().setAttribute("WarrantyEndDate", cal.getTime());
            AdfFacesContext.getCurrentInstance().addPartialTarget(warrExpiryDateHeader); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setNextMaintainDateHeader(){
        try {
            String month = null;
            try {
                month = getMaintainPeriodHeader().getValue().toString();
            } catch (Exception e) {
                ;
            }
            if ( month != null || month != "" || !month.equals("")){
                //adding months from Warranty Start Date
                SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(warrStartDateHeader.getValue().toString()));
                cal.add(Calendar.MONTH, Integer.parseInt(month));
                System.out.println("Calculated Date: " + cal.getTime());
                appM.getMnjWarrantyManagementHeaderVO1().getCurrentRow().setAttribute("NextMaintainDate", cal.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }       
    }

    public void setWarrStartDateHeader(RichInputDate warrStartDateHeader) {
        this.warrStartDateHeader = warrStartDateHeader;
    }

    public RichInputDate getWarrStartDateHeader() {
        return warrStartDateHeader;
    }

    public void setWarrPeriodHeader(RichInputText warrPeriodHeader) {
        this.warrPeriodHeader = warrPeriodHeader;
    }

    public RichInputText getWarrPeriodHeader() {
        return warrPeriodHeader;
    }

    public void setWarrExpiryDateHeader(RichInputDate warrExpiryDateHeader) {
        this.warrExpiryDateHeader = warrExpiryDateHeader;
    }

    public RichInputDate getWarrExpiryDateHeader() {
        return warrExpiryDateHeader;
    }

    public void warrPeriodHeaderValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        try {
            System.out.println("Changed Date: " + valueChangeEvent.getNewValue());
            setWarrantyExpiryDateHeader(valueChangeEvent.getNewValue().toString()); //calling method overloading
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWarrStartDateLine(RichInputDate warrStartDateLine) {
        this.warrStartDateLine = warrStartDateLine;
    }

    public RichInputDate getWarrStartDateLine() {
        return warrStartDateLine;
    }

    public void setWarrPeriodLine(RichInputText warrPeriodLine) {
        this.warrPeriodLine = warrPeriodLine;
    }

    public RichInputText getWarrPeriodLine() {
        return warrPeriodLine;
    }

    public void setWarrExpiryDateLine(RichOutputText warrExpiryDateLine) {
        this.warrExpiryDateLine = warrExpiryDateLine;
    }

    public RichOutputText getWarrExpiryDateLine() {
        return warrExpiryDateLine;
    }

    public void warrStartDateLineValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        try {
            System.out.println("Changed Date: " + valueChangeEvent.getNewValue());
            
            setWarrantyExpiryDateLine(appM.getMnjWarrantyManagementLineVO1(), valueChangeEvent.getNewValue().toString());
            
            setNextMaintainDateLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setWarrantyExpiryDateLine(ViewObject vo, String changedWarrStartDate) throws ParseException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(changedWarrStartDate));
            cal.add(Calendar.MONTH, Integer.parseInt(warrPeriodLine.getValue().toString()));
            System.out.println("Warranty Expiry Date: " + cal.getTime());
            vo.getCurrentRow().setAttribute("WarrantyEndDate", cal.getTime());
            AdfFacesContext.getCurrentInstance().addPartialTarget(warrExpiryDateLine); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * method overloading
     * @param changedWarrPeriodL
     * @throws ParseException
     */
    public void setWarrantyExpiryDateLine(String changedWarrPeriodL) throws ParseException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(warrStartDateLine.getValue().toString()));
            cal.add(Calendar.MONTH, Integer.parseInt(changedWarrPeriodL));
            System.out.println("Warranty Expiry Date: " + cal.getTime());
            appM.getMnjWarrantyManagementLineVO1().getCurrentRow().setAttribute("WarrantyEndDate", cal.getTime());
            AdfFacesContext.getCurrentInstance().addPartialTarget(warrExpiryDateLine); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setNextMaintainDateLine(){
        try {
            String month = null;
            try {
                month = getMaintainPeriodLine().getValue().toString();
            } catch (Exception e) {
                ;
            }
            if ( month != null || month != "" || !month.equals("")){
                //Adding Months From Warranty Start Date
                SimpleDateFormat dateFormat = new SimpleDateFormat ("dd-MMM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateFormat.parse(warrStartDateLine.getValue().toString()));
                cal.add(Calendar.MONTH, Integer.parseInt(month));
                System.out.println("Calculated Date: " + cal.getTime());
                appM.getMnjWarrantyManagementLineVO1().getCurrentRow().setAttribute("NextMaintainDate", cal.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }       
    }
    
    /**
     *
     * @param valueChangeEvent
     */
    public void warrPeriodLineValueChangeEvent(ValueChangeEvent valueChangeEvent) {
        try {
            System.out.println("Changed Date: " + valueChangeEvent.getNewValue());
            setWarrantyExpiryDateLine(valueChangeEvent.getNewValue().toString()); //Calling Method overloading
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
