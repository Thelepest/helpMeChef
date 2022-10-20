import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { QuantityService } from '../service/quantity.service';

import { QuantityComponent } from './quantity.component';

describe('Quantity Management Component', () => {
  let comp: QuantityComponent;
  let fixture: ComponentFixture<QuantityComponent>;
  let service: QuantityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [QuantityComponent],
    })
      .overrideTemplate(QuantityComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(QuantityComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(QuantityService);

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
    expect(comp.quantities?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
