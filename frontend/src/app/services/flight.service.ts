import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Flight {
  id: number;
  flightNumber: string;
  airline: string | null;
  destination: string;
  scheduledTime: string;
  delayMinutes: number;
  cancelled: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class FlightService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getFlights(page: number = 0, size: number = 5): Observable<Flight[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<Flight[]>(`${this.apiUrl}/flights`, { params });
  }

  getAllFlights(): Observable<Flight[]> {
    return this.http.get<Flight[]>(`${this.apiUrl}/flights/all`);
  }

  getDelayedPercentage(): Observable<{ percentage: number }> {
    return this.http.get<{ percentage: number }>(`${this.apiUrl}/stats/delayed-percentage`);
  }
}

// Made with Bob
