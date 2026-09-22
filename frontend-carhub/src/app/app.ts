import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Reclamos } from './components/reclamos/reclamos';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Reclamos],
  templateUrl: 'app.html',
  styleUrl: 'app.css',
})
export class App {
  protected readonly title = signal('frontendCarhub');
}
