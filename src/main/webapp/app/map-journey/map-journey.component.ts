import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { Loader } from "@googlemaps/js-api-loader"

@Component({
  selector: 'jhi-map-journey',
  templateUrl: './map-journey.component.html',
  styleUrls: ['./map-journey.component.scss'],
})
export class MapJourneyComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  map: google.maps.Map | null = null;
  journeyPath: google.maps.Polyline | null = null;
  timer: NodeJS.Timer | null = null;
  startPoint: { lat: number, lng: number } | null = null;
  endPoint: { lat: number, lng: number } | null = null;
  startMarker: google.maps.Marker | null = null;
  lastMarker: google.maps.Marker | null = null;

  private readonly destroy$ = new Subject<void>();

  constructor(private accountService: AccountService, private router: Router) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));

    this.loadMap();
  }

  loadMap(): void {
    const loader = new Loader({
      apiKey: "AIzaSyB41DRUbKWJHPxaFjMAwdrzWzbVKartNGg",
      version: "weekly",
      // ...additionalOptions,
    });

    loader.load().then(() => {
      this.startPoint = { lat: 37.772, lng: -122.214 };
      this.map = new google.maps.Map(document.getElementById("map") as HTMLElement, {
        center: this.startPoint,
        zoom: 14,
      });
      const points = [
        this.startPoint,
      ];

      this.journeyPath = new google.maps.Polyline({
        path: points,
        geodesic: true,
        strokeColor: "#ff6060",
        strokeOpacity: 1.0,
        strokeWeight: 1.5,
      });

      this.journeyPath.setMap(this.map);

      this.startMarker = new google.maps.Marker({
        position: new google.maps.LatLng(this.startPoint),
        title: "Start point",
        map: this.map,
      });

      this.initTimer();
    });
  }

  initTimer(): void {
    this.timer = setInterval(() => this.autoDraw(), 5000);
  }

  autoDraw(): void {
    const point = this.randomLocation();
    if (point) {
      this.removeLastMarker();
      this.addLocation(point);
      this.addMarker(point);
    }
  }

  removeLastMarker(): void {
    this.lastMarker?.setMap(null);
  }

  addMarker(latlng: google.maps.LatLng): void {
    this.lastMarker = new google.maps.Marker({
      position: new google.maps.LatLng(latlng),
      title: "Current location",
      icon: this.svgMarker(latlng),
      animation: google.maps.Animation.BOUNCE,
      map: this.map,
    });
  }

  addLocation(latlng: google.maps.LatLng): void {
    this.journeyPath?.getPath().push(latlng);
  }

  randomLocation(): google.maps.LatLng | null {
    const path = this.journeyPath?.getPath();
    const endPoint = path?.getAt(path.getLength() - 1);
    if (!endPoint) {
      return null;
    }
    const nextLat = +(endPoint.lat()) + Math.random()/100;
    const nextLng = +(endPoint.lng()) + Math.random()/100;

    return new google.maps.LatLng({lat: nextLat, lng: nextLng});
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  svgMarker(latlng: google.maps.LatLng): any {
    return {
      path: "M10.453 14.016l6.563-6.609-1.406-1.406-5.156 5.203-2.063-2.109-1.406 1.406zM12 2.016q2.906 0 4.945 2.039t2.039 4.945q0 1.453-0.727 3.328t-1.758 3.516-2.039 3.070-1.711 2.273l-0.75 0.797q-0.281-0.328-0.75-0.867t-1.688-2.156-2.133-3.141-1.664-3.445-0.75-3.375q0-2.906 2.039-4.945t4.945-2.039z",
      fillColor: "#44c3c5",
      fillOpacity: 1,
      scale: 2,
      anchor: latlng,
    };
  }
}
