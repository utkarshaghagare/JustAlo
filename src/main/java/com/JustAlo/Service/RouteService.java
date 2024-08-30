package com.JustAlo.Service;

import com.JustAlo.Entity.Route;
import com.JustAlo.Repo.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;
    public Optional<Route> getRouteById(Long routeId) {
        return routeRepository.findById(routeId);
    }

    public Route addRoute(Route route) {
        return routeRepository.save(route);
    }

    public List<Route> getAllRouts() {

        return  routeRepository.findAll();
    }
}
