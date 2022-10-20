import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { UncompatibleIRCategoryService } from '../service/uncompatible-ir-category.service';

import { UncompatibleIRCategoryComponent } from './uncompatible-ir-category.component';

describe('UncompatibleIRCategory Management Component', () => {
  let comp: UncompatibleIRCategoryComponent;
  let fixture: ComponentFixture<UncompatibleIRCategoryComponent>;
  let service: UncompatibleIRCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [UncompatibleIRCategoryComponent],
    })
      .overrideTemplate(UncompatibleIRCategoryComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UncompatibleIRCategoryComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UncompatibleIRCategoryService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.uncompatibleIRCategories?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
