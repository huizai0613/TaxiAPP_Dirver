package com.wutong.taxiapp.net.service;

interface IBackService{
	boolean sendMessage(String message);
	boolean sendImageMessage(String path);
}