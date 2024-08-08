package com.JustAlo.Service;

import com.JustAlo.Entity.Route;
import com.JustAlo.Repo.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;
    public Optional<Route> getRouteById(Long routeId) {
        return routeRepository.findById(routeId);
    }
}
