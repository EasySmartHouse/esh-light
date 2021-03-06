/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.easysmarthouse.maxim.network;

import net.easysmarthouse.maxim.network.adapter.AdapterProvider;
import net.easysmarthouse.maxim.network.extension.OneWireSearchExtension;
import net.easysmarthouse.network.AbstractStorableNetworkManager;
import net.easysmarthouse.network.util.AddressHelper;
import net.easysmarthouse.network.predicate.NetworkSearchPredicate;
import net.easysmarthouse.network.exception.NetworkException;
import net.easysmarthouse.network.extension.ConversionExtension;
import net.easysmarthouse.network.predicate.PredicateVisitor;
import net.easysmarthouse.network.predicate.SimplePredicateVisitor;
import net.easysmarthouse.provider.device.Device;
import net.easysmarthouse.provider.device.converter.DeviceConverter;
import com.dalsemi.onewire.OneWireException;
import com.dalsemi.onewire.container.OneWireContainer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author rusakovich
 */
public class OneWireNetworkManager extends AbstractStorableNetworkManager {

    private AdapterProvider adapterProvider;
    private OneWireSearchExtension searchExtension;
    private ConversionExtension conversionExtension;
    private DeviceConverter deviceConverter;

    public AdapterProvider getAdapterProvider() {
        return adapterProvider;
    }

    public void setAdapterProvider(AdapterProvider adapterProvider) {
        this.adapterProvider = adapterProvider;
    }

    @Override
    public void startSession() throws NetworkException {
        try {
            //To allow for multi-thread access to the 1-Wire network
            adapterProvider.getAdapter().beginExclusive(true);
        } catch (OneWireException ex) {
            throw new NetworkException("Network start session exception", ex);
        }
    }

    @Override
    public void endSession() throws NetworkException {
        try {
            //To allow for multi-thread access to the 1-Wire network
            adapterProvider.getAdapter().endExclusive();
        } catch (OneWireException ex) {
            throw new NetworkException("Network end session exception", ex);
        }
    }

    @Override
    public List<Device> search(NetworkSearchPredicate predicate) throws NetworkException {
        long address = Long.MIN_VALUE;
        try {
            PredicateVisitor predicateVisitor = new SimplePredicateVisitor();
            address = AddressHelper.toLong(predicateVisitor.getAddress(predicate));
        } catch (Exception ex) {
            return Collections.EMPTY_LIST;
        }

        OneWireContainer container = searchExtension.getDevice(address);
        Device device = deviceConverter.getDevice(container);
        if (device == null) {
            return Collections.EMPTY_LIST;
        }

        final List<Device> devices = new LinkedList<>();
        devices.add(device);

        return devices;
    }

    @Override
    public List<Device> getDevices() throws NetworkException {
        List<OneWireContainer> containers = searchExtension.getDevices();
        return deviceConverter.getDevices(containers);
    }

    @Override
    public List<Long> getDevicesAddresses() throws NetworkException {
        return searchExtension.getDevicesAddresses();
    }

    @Override
    public boolean isDevicePresent(String address) throws NetworkException {
        long addressL = Long.MIN_VALUE;
        try {
            addressL = AddressHelper.toLong(address);
        } catch (Exception ex) {
            return false;
        }

        return searchExtension.isDevicePresent(addressL);
    }

    @Override
    public void refreshDevices() throws NetworkException {
        try {
            searchExtension.refresh();
        } catch (OneWireException ex) {
            throw new NetworkException(ex);
        }
    }

    public OneWireSearchExtension getSearchExtension() {
        return searchExtension;
    }

    public void setSearchExtension(OneWireSearchExtension searchExtension) {
        this.searchExtension = searchExtension;
    }

    public DeviceConverter getDeviceConverter() {
        return deviceConverter;
    }

    public void setDeviceConverter(DeviceConverter deviceConverter) {
        this.deviceConverter = deviceConverter;
    }

    @Override
    public ConversionExtension getConversionExtension() {
        return conversionExtension;
    }

    public void setConversionExtension(ConversionExtension conversionExtension) {
        this.conversionExtension = conversionExtension;
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }
}
