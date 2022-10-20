import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ToolDetailComponent } from './tool-detail.component';

describe('Tool Management Detail Component', () => {
  let comp: ToolDetailComponent;
  let fixture: ComponentFixture<ToolDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ToolDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ tool: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ToolDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ToolDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load tool on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.tool).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
