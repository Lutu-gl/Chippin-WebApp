import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-counter',
  templateUrl: './counter.component.html',
  styleUrl: './counter.component.scss'
})
export class CounterComponent {

  @Input() endValue: number = 0;
  @Input() label: string = '';
  @Input() afterLabel: string = '';
  currentCount: number = 0;

  ngOnInit() {
    this.animateCount();
  }

  animateCount() {
    const duration = 2000; // Dauer der Animation in Millisekunden
    const frameDuration = 1000 / 60; // Dauer eines Frames (bei 60fps)
    const totalFrames = Math.round(duration / frameDuration);
    const increment = this.endValue / totalFrames;
    let frame = 0;

    const counter = setInterval(() => {
      frame++;
      this.currentCount += increment;

      if (frame === totalFrames) {
        clearInterval(counter);
        this.currentCount = this.endValue; // Sicherstellen, dass der Endwert erreicht wird
      }
    }, frameDuration);
  }
}
