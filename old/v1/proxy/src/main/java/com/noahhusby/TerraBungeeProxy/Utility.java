/*
 * TerraBungee - Proxy
 * Copyright (c) 2020 Saghetti & Noah Husby
 *
 * Utility.java
 */

package com.noahhusby.TerraBungeeProxy;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class Utility {
	public static InetSocketAddress makeInetSocketAddressFromString(String address) {
		try {
			URI uri = new URI("abc://" + address);
			String host = uri.getHost();
			int port = uri.getPort();
			if (uri.getHost() == null || uri.getPort() == -1) {
				return null;
			}
			return new InetSocketAddress (host, port);
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
