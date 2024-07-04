package com.example.demo.service;

public interface IConvierteDatos {

    <T> T obtenerDatos(String json, Class<T> clase);
}
