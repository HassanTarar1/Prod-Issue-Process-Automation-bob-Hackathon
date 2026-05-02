import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface BobAction {
  id: string;
  type: 'analysis' | 'fix-generation' | 'pr-creation' | 'test-execution' | 'notification' | 'error';
  title: string;
  description: string;
  timestamp: Date;
  status: 'in-progress' | 'success' | 'error' | 'warning';
  details?: any;
  duration?: number;
}

@Injectable({
  providedIn: 'root'
})
export class BobActionLogService {
  private actions = new BehaviorSubject<BobAction[]>([]);
  private actionCounter = 0;

  constructor() { }

  getActions(): Observable<BobAction[]> {
    return this.actions.asObservable();
  }

  addAction(action: Omit<BobAction, 'id' | 'timestamp'>): string {
    const id = `action-${++this.actionCounter}-${Date.now()}`;
    const newAction: BobAction = {
      ...action,
      id,
      timestamp: new Date()
    };
    
    const currentActions = this.actions.value;
    this.actions.next([newAction, ...currentActions]);
    
    return id;
  }

  updateAction(id: string, updates: Partial<BobAction>): void {
    const currentActions = this.actions.value;
    const index = currentActions.findIndex(a => a.id === id);
    
    if (index !== -1) {
      const updatedAction = { ...currentActions[index], ...updates };
      const newActions = [...currentActions];
      newActions[index] = updatedAction;
      this.actions.next(newActions);
    }
  }

  clearActions(): void {
    this.actions.next([]);
  }

  getActionCount(): number {
    return this.actions.value.length;
  }
}

// Made with Bob