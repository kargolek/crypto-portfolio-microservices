import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private isLoadingSubject = new BehaviorSubject<boolean>(false);

  isLoading$ = this.isLoadingSubject.asObservable();

  show(): void {
    console.log('LoadingService.show()');
    this.isLoadingSubject.next(true);
  }

  hide(): void {
    console.log('LoadingService.hide()');
    this.isLoadingSubject.next(false);
  }
}
