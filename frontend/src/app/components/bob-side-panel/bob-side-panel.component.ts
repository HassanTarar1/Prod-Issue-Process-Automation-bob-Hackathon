import { Component, OnInit, OnDestroy } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { BobActionLogService, BobAction } from '../../services/bob-action-log.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-bob-side-panel',
  templateUrl: './bob-side-panel.component.html',
  styleUrls: ['./bob-side-panel.component.scss'],
  animations: [
    trigger('slideInOut', [
      state('in', style({
        transform: 'translateX(0)',
        opacity: 1
      })),
      state('out', style({
        transform: 'translateX(-100%)',
        opacity: 0
      })),
      transition('in => out', animate('300ms ease-in-out')),
      transition('out => in', animate('300ms ease-in-out'))
    ]),
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-10px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class BobSidePanelComponent implements OnInit, OnDestroy {
  isOpen = false;
  actions: BobAction[] = [];
  private subscription?: Subscription;
  private autoOpenTimeout?: any;

  constructor(private bobActionLogService: BobActionLogService) { }

  ngOnInit(): void {
    this.subscription = this.bobActionLogService.getActions().subscribe(actions => {
      const hadActions = this.actions.length > 0;
      this.actions = actions;
      
      // Auto-open panel when new action is added
      if (actions.length > 0 && actions.length > (hadActions ? this.actions.length : 0)) {
        this.autoOpen();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.autoOpenTimeout) {
      clearTimeout(this.autoOpenTimeout);
    }
  }

  togglePanel(): void {
    this.isOpen = !this.isOpen;
  }

  autoOpen(): void {
    if (!this.isOpen) {
      this.isOpen = true;
    }
  }

  clearActions(): void {
    this.bobActionLogService.clearActions();
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'success': return 'check_circle';
      case 'error': return 'error';
      case 'warning': return 'warning';
      case 'in-progress': return 'hourglass_empty';
      default: return 'info';
    }
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'success': return 'success';
      case 'error': return 'error';
      case 'warning': return 'warning';
      case 'in-progress': return 'primary';
      default: return 'default';
    }
  }

  getTypeIcon(type: string): string {
    switch (type) {
      case 'analysis': return 'analytics';
      case 'fix-generation': return 'build';
      case 'pr-creation': return 'merge';
      case 'test-execution': return 'science';
      case 'notification': return 'notifications';
      case 'error': return 'error_outline';
      default: return 'info';
    }
  }

  formatTimestamp(timestamp: Date): string {
    const now = new Date();
    const diff = now.getTime() - new Date(timestamp).getTime();
    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);

    if (seconds < 60) return `${seconds}s ago`;
    if (minutes < 60) return `${minutes}m ago`;
    if (hours < 24) return `${hours}h ago`;
    return new Date(timestamp).toLocaleString();
  }

  formatDuration(duration?: number): string {
    if (!duration) return '';
    if (duration < 1000) return `${duration}ms`;
    return `${(duration / 1000).toFixed(2)}s`;
  }

  trackByActionId(index: number, action: BobAction): string {
    return action.id;
  }
}

// Made with Bob