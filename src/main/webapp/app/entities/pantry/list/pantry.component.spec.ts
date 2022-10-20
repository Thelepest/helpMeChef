import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { PantryService } from '../service/pantry.service';

import { PantryComponent } from './pantry.component';

describe('Pantry Management Component', () => {
  let comp: PantryComponent;
  let fixture: ComponentFixture<PantryComponent>;
  let service: PantryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [PantryComponent],
    })
      .overrideTemplate(PantryComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PantryComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PantryService);

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
    expect(comp.pantries?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
