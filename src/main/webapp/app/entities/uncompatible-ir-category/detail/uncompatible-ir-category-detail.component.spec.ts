import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UncompatibleIRCategoryDetailComponent } from './uncompatible-ir-category-detail.component';

describe('UncompatibleIRCategory Management Detail Component', () => {
  let comp: UncompatibleIRCategoryDetailComponent;
  let fixture: ComponentFixture<UncompatibleIRCategoryDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UncompatibleIRCategoryDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ uncompatibleIRCategory: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UncompatibleIRCategoryDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UncompatibleIRCategoryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load uncompatibleIRCategory on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.uncompatibleIRCategory).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
