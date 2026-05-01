import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AutoFixRequest {
  logLine: string;
  repo?: string;
  filePath?: string;
}

export interface AutoFixResponse {
  fixable: boolean;
  errorType: string;
  fixGenerated: boolean;
  fixSnippet: string;
  prUrl: string;
  slackNotified: boolean;
  testPassed?: boolean;
  message: string;
}

export interface RecordTestRequest {
  url?: string;
}

export interface RecordTestResponse {
  success: boolean;
  testFilePath: string;
  message: string;
}

export interface SystemStatus {
  playwrightInstalled: boolean;
  githubConfigured: boolean;
  slackConfigured: boolean;
  totalFixesGenerated: number;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class AutoFixService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  triggerAutoFix(request: AutoFixRequest): Observable<AutoFixResponse> {
    return this.http.post<AutoFixResponse>(`${this.apiUrl}/auto-fix`, request);
  }

  recordTest(request: RecordTestRequest): Observable<RecordTestResponse> {
    return this.http.post<RecordTestResponse>(`${this.apiUrl}/record-test`, request);
  }

  getSystemStatus(): Observable<SystemStatus> {
    return this.http.get<SystemStatus>(`${this.apiUrl}/auto-fix/status`);
  }
}

// Made with Bob
