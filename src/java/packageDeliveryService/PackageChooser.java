/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package packageDeliveryService;

import com.mycompany.deliverysystem.entities.DirectedPackage;
import java.util.List;

/**
 *
 * @author rafael,dominik
 */
public interface PackageChooser {
    public List<org.skspackage.schema._2013.deliveryservice.Package> getDevileredPackageByRegion(String regionId, List<org.skspackage.schema._2013.deliveryservice.Package> p);
    public Iterable<DirectedPackage> getPackagesByRegionKey(String regionKey);
}
