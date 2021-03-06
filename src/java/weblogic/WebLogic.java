/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package weblogic;

import com.mycompany.deliverysystem.entities.DeliveryRegion;
import com.mycompany.deliverysystem.entities.DirectedPackage;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepository;
import com.mycompany.deliverysystem.repositories.DeliveryRegionRepositoryDB;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepository;
import com.mycompany.deliverysystem.repositories.DirectedPackageRepositoryDB;
import deliveryRegion.DeliveryRegionService;
import deliveryRegion.DeliveryRegionServiceDB;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import packageDeliveryService.PackageChooser;
import packageDeliveryService.PackageChooserDB;
import org.skspackage.schema._2013.deliveryservice.Package;

/**
 *
 * @author rafael
 */
public class WebLogic {
    
    private PackageChooser packageChooser;
    private DeliveryRegionService regionService;
    
    public WebLogic()
    {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("deliverysystem");
        EntityManager entityManager = factory.createEntityManager();
        packageChooser = new PackageChooserDB(entityManager);
        regionService = new DeliveryRegionServiceDB(entityManager);
    }
    
    public Iterable<DirectedPackage> getUndeliveredPackages(String regionKey)
    {
        List<DirectedPackage> p = new ArrayList<DirectedPackage>();
        Iterable<DirectedPackage> packages = packageChooser.getPackagesByRegionKey(regionKey);
        if (packages == null) // no packages found
            return p;
        for (DirectedPackage dp : packages)
        {
            if (!dp.isDelivered())
                p.add(dp);
        }
        return p;
    }
    
    public int deliverPackages(String regionKey)
    {
        List<Package> list = new ArrayList<Package>();
        packageChooser.getDevileredPackageByRegion(regionKey, list);
        if (list.size() > 0) // if something could have been delivered
            return list.size();
        
        return -1; // if not
    }
    
    public Iterable<DeliveryRegion> getDeliveryRegions()
    {
        return regionService.getAllDeliveryRegions();
    }
    
    public String getReadOnlyDiv(String id, String type, String content)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<div id='" + id + "_" + type + "' class='readonly'>");
        sb.append(content);
        sb.append("</div>");
        
        return sb.toString();
    }
    
    public String getEditField(String id, String type, String content)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<input type='text' id='" + id + "_" + type + "_input' class='editfield' value='" + content + "'/>");
        return sb.toString();
    }
    
    public void updateRegion(String id, String externalId, String longitude, String latitude)
    {
        System.out.println(longitude + ", " + latitude + ", " + externalId);
        DeliveryRegion region = new DeliveryRegion();
        region.setId(Long.parseLong(id));
        region.setExternal_id(externalId);
        region.setLongitude(Double.parseDouble(longitude));
        region.setLatitude(Double.parseDouble(latitude));
        
        regionService.updateAndReorderDeliveryRegion(Long.parseLong(id), region);
    }
    
    public void deleteRegion(String id)
    {
        long regionId = Long.parseLong(id);
        regionService.deleteRegion(regionId);
    }
    
    public void addRegions(String xml)
    {
        // TODO implement
        URL url;
        try {
            url = new URL("http://localhost:8080/DeliverySystemRegionImport/webresources/import");
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;

            byte[] requestXML = xml.getBytes();

            // Set the appropriate HTTP parameters.
            httpConn.setRequestProperty("Content-Length", String.valueOf(requestXML.length));
            httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);

            // Send the String that was read into postByte.
            OutputStream out = httpConn.getOutputStream();
            out.write(requestXML);
            out.close();

            // Read the response and write it to standard out.
           InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
   
        } catch (MalformedURLException ex) {
            Logger.getLogger(WebLogic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(WebLogic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WebLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
