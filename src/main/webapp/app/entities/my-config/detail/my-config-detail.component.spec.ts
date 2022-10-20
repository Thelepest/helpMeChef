import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MyConfigDetailComponent } from './my-config-detail.component';

describe('MyConfig Management Detail Component', () => {
  let comp: MyConfigDetailComponent;
  let fixture: ComponentFixture<MyConfigDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MyConfigDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ myConfig: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MyConfigDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MyConfigDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load myConfig on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.myConfig).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
