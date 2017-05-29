package it.polito.parser;

public class Constants {
	
	//Methods
	public static final String MAIN_NF_METHOD = "onReceivedPacket";
	public static final String ENTRY_SETTER = "setValue";
	public static final String ENTRY_GETTER = "getValue";
	public static final String SET_FIELD_METHOD = "setField";
	public static final String GET_FIELD_METHOD = "getField";
	public static final String SATISFY_METHOD_NAME = "satisfy";
	public static final String MATCH_ENTRY_METHOD_NAME = "matchEntry";
	public static final String SEND_METHOD_NAME = "sendPacket";
	public static final String STORE_ENTRY_METHOD_NAME = "storeEntry";
	public static final String DATA_DRIVEN = "setDataDriven";
	public static final String IS_INTERNAL_METHOD = "isInternal";
	public static final String EQUALS_FIELD_METHOD = "equalsField";
	public static final String SET_TYPES = "setTypes";
	
	//Function' parameters  --> in Nat.java
	public static final String INTERNAL_INTERFACE = "internalInterface";
	public static final String EXTERNAL_INTERFACE = "externalInterface";
	public static final String INTERFACE_PARAMETER = "iface";
	public static final String PACKET_PARAMETER = "packet";
	
	//Types
	public static final String TABLE_ENTRY_TYPE = "TableEntry";
	public static final String PACKET_TYPE = "Packet";
	public static final String INTERFACE_TYPE = "Interface";
	public static final String TABLE_TYPE = "Table";
	public static final String ROUTING_RESULT_CLASS = "RoutingResult";
	
	//Packet fields    -->PacketField
	public static final String ETH_SOURCE = "ETH_SRC";
	public static final String ETH_DESTINATION = "ETH_DST";
	public static final String IP_SOURCE = "IP_SRC";
	public static final String IP_DESTINATION = "IP_DST";
	public static final String PORT_SOURCE = "PORT_SRC";
	public static final String PORT_DESTINATION = "PORT_DST";
	public static final String TRANSPORT_PROTOCOL = "TRANSPORT_PROTOCOL";
	public static final String APPLICATION_PROTOCOL = "APPLICATION_PROTOCOL";
	public static final String L7DATA = "L7DATA"; 
	
	//Z3 packet fields
	public static final String Z3_ETH_SOURCE = "src_eth";
	public static final String Z3_ETH_DESTINATION = "dst_eth";
	public static final String Z3_IP_SOURCE = "src";
	public static final String Z3_IP_DESTINATION = "dest";
	public static final String Z3_PORT_SOURCE = "src_port";
	public static final String Z3_PORT_DESTINATION = "dst_port";
	public static final String Z3_TRANSPORT_PROTOCOL = "transport_protocol";
	public static final String Z3_APPLICATION_PROTOCOL = "proto";
	public static final String Z3_L7DATA = "application_data";
	
	//Placeholder
	public static final String ANY_VALUE = "ANY_VALUE";
	public static final String PLACE_HOLDER = "placeholder";
	public static final String NONE = "None";
	public static final String INTERNAL = "internal";
	public static final String EXTERNAL = "external";
	
	//Field type enums   in tables-->TableTypes
	public static final String ENUM_ETHERNET = "Ethernet";
	public static final String ENUM_IP = "Ip";
	public static final String ENUM_PORT = "Port";
	public static final String ENUM_TRANSPORT_PROTOCOL = "TransportProtocol";
	public static final String ENUM_APPLICATION_PROTOCOL = "ApplicationProtocol";
	public static final String ENUM_APPLICATION_DATA = "ApplicationData";
	public static final String ENUM_GENERIC = "Generic";
	
}
