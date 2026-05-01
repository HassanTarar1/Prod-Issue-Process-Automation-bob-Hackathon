import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FlightService, Flight } from './services/flight.service';
import { AutoFixService, AutoFixRequest, AutoFixResponse } from './services/auto-fix.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'IBM Bob - Flight Dashboard';
  
  flights: Flight[] = [];
  displayedColumns: string[] = ['flightNumber', 'airline', 'destination', 'scheduledTime', 'delayMinutes', 'cancelled', 'actions'];
  
  isLoading = false;
  pageSize = 5;
  pageIndex = 0;
  totalFlights = 50;
  
  // Auto-fix panel
  showAutoFixPanel = false;
  autoFixResult: AutoFixResponse | null = null;
  isFixing = false;
  
  // System status
  systemStatus: any = null;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(
    private flightService: FlightService,
    private autoFixService: AutoFixService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadFlights();
    this.loadSystemStatus();
  }

  loadFlights(): void {
    this.isLoading = true;
    this.flightService.getFlights(this.pageIndex, this.pageSize).subscribe({
      next: (data) => {
        this.flights = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading flights:', error);
        this.showNotification('Error loading flights: ' + error.message, 'error');
        this.isLoading = false;
      }
    });
  }

  loadSystemStatus(): void {
    this.autoFixService.getSystemStatus().subscribe({
      next: (status) => {
        this.systemStatus = status;
      },
      error: (error) => {
        console.error('Error loading system status:', error);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadFlights();
  }

  triggerAutoFix(flight: Flight): void {
    this.isFixing = true;
    this.showAutoFixPanel = true;
    
    // Simulate a log line for the flight error
    const logLine = `{"timestamp":"${new Date().toISOString()}","errorType":"NullPointerException","message":"Cannot invoke method on null airline","stackTrace":["com.hackathon.service.FlightService.getFlights(FlightService.java:45)"]}`;
    
    const request: AutoFixRequest = {
      logLine: logLine,
      repo: 'demo/flight-dashboard',
      filePath: 'src/main/java/com/hackathon/service/FlightService.java'
    };

    this.autoFixService.triggerAutoFix(request).subscribe({
      next: (result) => {
        this.autoFixResult = result;
        this.isFixing = false;
        if (result.fixGenerated) {
          this.showNotification('Fix generated successfully! PR created.', 'success');
        } else {
          this.showNotification('Error is not auto-fixable', 'warning');
        }
        this.loadSystemStatus(); // Refresh status
      },
      error: (error) => {
        console.error('Error triggering auto-fix:', error);
        this.showNotification('Error triggering auto-fix: ' + error.message, 'error');
        this.isFixing = false;
      }
    });
  }

  recordTest(): void {
    this.showNotification('Launching Playwright Codegen...', 'info');
    
    this.autoFixService.recordTest({ url: 'http://localhost:4200' }).subscribe({
      next: (result) => {
        if (result.success) {
          this.showNotification(`Test recorded: ${result.testFilePath}`, 'success');
        } else {
          this.showNotification(result.message, 'warning');
        }
      },
      error: (error) => {
        console.error('Error recording test:', error);
        this.showNotification('Error recording test: ' + error.message, 'error');
      }
    });
  }

  closeAutoFixPanel(): void {
    this.showAutoFixPanel = false;
    this.autoFixResult = null;
  }

  showNotification(message: string, type: 'success' | 'error' | 'warning' | 'info'): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: [`snackbar-${type}`]
    });
  }

  getStatusColor(status: boolean): string {
    return status ? 'primary' : 'warn';
  }

  getStatusIcon(status: boolean): string {
    return status ? 'check_circle' : 'cancel';
  }
}

// Made with Bob
