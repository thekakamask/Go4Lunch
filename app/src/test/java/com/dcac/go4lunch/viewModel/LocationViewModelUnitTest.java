package com.dcac.go4lunch.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dcac.go4lunch.repository.LocationRepository;
import com.dcac.go4lunch.utils.LiveDataTestUtils;
import com.dcac.go4lunch.viewModels.LocationViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LocationViewModelUnitTest {

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    public LocationRepository mockLocationRepository;

    @Mock Location mockLocation;

    LocationViewModel SUT;

    MutableLiveData<Location> liveDataLocation = new MutableLiveData<>();
    @Before
    public void setup() {

        doReturn(liveDataLocation)
                .when(mockLocationRepository)
                .getLastKnownLocation();

        when(mockLocation.getLatitude()).thenReturn(34.052235);
        when(mockLocation.getLongitude()).thenReturn(-118.243683);

        SUT = new LocationViewModel(mockLocationRepository);
    }

    @Test
    public void testGetLocationLiveDataSuccess() throws InterruptedException {
        liveDataLocation.setValue(mockLocation);

        Location resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getLocationLiveData());


        assertEquals("The latitude should match", 34.052235, resultAwaited.getLatitude(), 0.001);
        assertEquals("The longitude should match", -118.243683, resultAwaited.getLongitude(), 0.001);
    }

    @Test
    public void testGetLocationLiveDataFailure() throws InterruptedException {
        liveDataLocation.setValue(null);

        // Act
        Location resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getLocationLiveData());

        // Assert
        assertNull("Location should be null", resultAwaited);
    }


}
