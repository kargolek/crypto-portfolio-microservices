import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, tap } from 'rxjs';
import { TokenCrypto } from '../model/token-crypto';

@Injectable({
  providedIn: 'root'
})
export class PriceService {

  constructor(private http: HttpClient) { }

  private getPriceURL = 'http://localhost:8080/api/v1/cryptocurrency';

  getPrice(): Observable<TokenCrypto[]> {
    const data = this.http.get<TokenCrypto[]>(this.getPriceURL).pipe(
      catchError(this.handleError<TokenCrypto[]>('getPrice', []))
    );
    return data;
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error);
      return error;
    };
  }
}

