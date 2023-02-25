import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpResponse
} from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoadingService } from '../../service/loading/loading.service';

@Injectable()
export class LoadingInterceptor implements HttpInterceptor {

  constructor(private loadingService: LoadingService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    this.loadingService.show();
    return next.handle(request).pipe(tap({
      next: event => {
        if (event instanceof HttpResponse) {
          this.loadingService.hide(); // Hide the loading spinner when the request is complete
        }
      },
      error: error => {
        this.loadingService.hide(); // Hide the loading spinner when an error occurs
      }
    })
  );
  }
}
