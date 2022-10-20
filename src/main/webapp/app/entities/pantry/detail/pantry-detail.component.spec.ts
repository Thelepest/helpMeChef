import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PantryDetailComponent } from './pantry-detail.component';

describe('Pantry Management Detail Component', () => {
  let comp: PantryDetailComponent;
  let fixture: ComponentFixture<PantryDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PantryDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ pantry: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PantryDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PantryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load pantry on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.pantry).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
