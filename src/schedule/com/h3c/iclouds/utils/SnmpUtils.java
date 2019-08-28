package com.h3c.iclouds.utils;

import com.h3c.iclouds.exception.MessageException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * Created by zkf5485 on 2017/7/19.
 */
public class SnmpUtils {

    private String ip;

    private int port;

    private String octet;

    private int version;

    private Snmp snmp;

    private CommunityTarget target;

    public static void main(String[] args) {
        String ip = "10.16.16.1";
        int port = 161;
        int version = SnmpConstants.version2c;
        String octet = "public";
        SnmpUtils utils = new SnmpUtils(ip, octet, port, version);
        String oidGet = ".1.3.6.1.2.1.1.1.0";
//        System.out.println(utils.sendRequest(utils.createGetPdu(oidGet)));
        String oidWalk = ".1.3.6.1.2.1.2.2.1.2";
        utils.snmpWalk(oidWalk, 100000);
        utils.closeSnmp();
    }

    public SnmpUtils() {

    }

    public SnmpUtils(String ip, String octet, int port, int version) {
        this.ip = ip;
        this.octet = octet;
        this.port = port;
        this.version = version;
        this.initSnmp();
        this.initTarget();
    }

    public CommunityTarget initTarget() {
        if(target != null) {
            throw new MessageException("CommunityTarget 已经初始化完毕");
        }
        target = new CommunityTarget();
        target.setCommunity(new OctetString(this.octet));
        target.setVersion(this.version);
        target.setAddress(new UdpAddress(ip + "/" + port));
        target.setTimeout(10000);
//        target.setRetries(1);
        return target;
    }

    public Snmp initSnmp() {
        if(snmp != null) {
            throw new MessageException("Snmp 已经初始化完毕");
        }
        try {
            snmp = new Snmp(new DefaultUdpTransportMapping());
            snmp.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return snmp;
    }

    public PDU createGetPdu(String oid) {
        PDU pdu = new PDU();
        pdu.setType(PDU.GET);
        pdu.add(new VariableBinding(new OID(oid)));
        return pdu;
    }

    public String response(PDU response) {
        if(response == null) {
            LogUtils.warn(this.getClass(), this.toString() + " Send request time out.");
        } else {
            if(response.getErrorStatus() == PDU.noError) {
                Vector<? extends VariableBinding> vbs = response.getVariableBindings();
                for (VariableBinding vb : vbs) {
                    return vb.getVariable().toString();
                }
            } else {
                LogUtils.warn(this.getClass(), toString() + " Send request error:" + response.getErrorStatusText());
            }
        }
        return null;
    }

    public String sendRequest(String oid) {
        return this.sendRequest(this.createGetPdu(oid));
    }

    public String sendRequest(PDU pdu) {
        try {
            ResponseEvent responseEvent = snmp.send(pdu, target);
            PDU response = responseEvent.getResponse();
            return response(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TableEvent> snmpWalk(String oid, int end) {
        TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETNEXT));
        utils.setMaxNumRowsPerPDU(5);
        List<TableEvent> list = utils.getTable(target, new OID[]{new OID(oid)}, new OID("0"), new OID(String.valueOf(end)));
        list.forEach(event ->
            System.out.println(event.getIndex() + ":" + event.getColumns()[0].getVariable())
        );
        return list;
    }

    public Snmp getSnmp() {
        return snmp;
    }

    public void setSnmp(Snmp snmp) {
        this.snmp = snmp;
    }

    public CommunityTarget getTarget() {
        return target;
    }

    public void setTarget(CommunityTarget target) {
        this.target = target;
    }

    public void closeSnmp() {
        try {
            if(this.snmp != null)
                snmp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("SNMP IP:").append(this.ip)
                .append(" , Port:").append(this.port)
                .append(" , Octet:").append(this.octet)
                .append(" , Version:").append(this.version).append(".");
        return buff.toString();
    }
}
